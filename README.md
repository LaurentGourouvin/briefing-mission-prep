# NOVA — Projet d'entraînement microservices (prépa entretien 28/08/2026)

Squelette d'amorçage. **Le code métier n'est pas écrit** : tout ce qui a de la valeur d'oral
est en `// TODO` avec des hints. Tu remplis, on débriefe. Rien de sensible, domaine fictif.

> ⚠️ Ce README **est** la proposition d'archi + versions + `docker-compose` que la section
> « Démarrage » de ton prompt demande. **Valide (ou corrige) l'archi avant qu'on code l'Étape 1.**

---

## 1. Architecture

```
                       POST /missions
                            │
                            ▼
                 ┌────────────────────┐
                 │   mission-service  │  :8080   (orchestrateur SAGA + état persistant)
                 │  ────────────────  │
                 │  SAGA orchestrée   │
                 └───────┬─────┬──────┘
              SYNC (1)   │     │   SYNC (2)
        Feign+Resilience │     │  Feign
                         ▼     ▼
        ┌──────────────────┐  ┌────────────────────┐
        │  aeronef-service │  │  clearance-service │
        │      :8081       │  │       :8082        │
        │ reserve/libère   │  │ accorde/refuse     │
        │ (verrou opt/pess)│  │ (4xx métier/5xx)   │
        └──────────────────┘  └────────────────────┘
                         │
              ASYNC (3)  │ publie "MissionPlanifiee"  (après commit local)
                         ▼
                   ┌───────────┐        ┌────────────────────┐
                   │  Redpanda │───────▶│ telemetry-service  │ :8083
                   │  (Kafka)  │        │ consumer idempotent│
                   └───────────┘        │ + ingestion (stretch)
                                        └────────────────────┘

Postgres : 1 base par service (mission_db / aeronef_db / clearance_db).
```

**Où vit la SAGA ?** Dans `mission-service`, orchestration (pas chorégraphie) : un chef
d'orchestre qui appelle explicitement chaque étape et déclenche la compensation.

**Sync vs async :**
- `mission → aeronef` et `mission → clearance` : **synchrone** (on a besoin de la réponse
  immédiate pour décider de la suite de la SAGA).
- `mission → telemetry` : **asynchrone** via Kafka (le provisioning télémétrie n'a pas
  besoin d'être fait avant de répondre au client ; on découple).

**Flux SAGA :** réserver aéronef (sync) → clearance (sync) → publier `MissionPlanifiee` (async).
Si clearance **refuse** → **compensation** : libérer l'aéronef (idempotente, rejouable).

---

## 2. Versions retenues et pourquoi

| Composant | Version | Pourquoi |
|---|---|---|
| Java | **17** | Cible imposée NOVA. |
| Spring Boot | **3.3.5** | Ligne stable, Java 17+, BOM qui aligne le cœur Spring. |
| Spring Cloud | **2023.0.3** (Leyton) | **Train compatible Boot 3.3.x** (OpenFeign vient de là). Se tromper de train = autoconfig cassée au boot. |
| Resilience4j | **2.2.0** | Starter **tiers** `resilience4j-spring-boot3`, **non géré par le BOM Spring** → piloté par **son propre BOM** (le fix du footgun springdoc). |
| Spring Kafka | (via BOM Boot) | Producer/consumer ; version alignée par Boot, pas à fixer. |
| Redpanda | **v24.2.7** | Broker Kafka-compatible mono-binaire, **léger** (pas de JVM, pas de ZooKeeper) → adapté à une machine contrainte. |
| Postgres | **16** | Une base par service. |

**Matrice de compat à savoir citer :** `Boot 3.3.x ⇄ Spring Cloud 2023.0.x`. C'est LA erreur
classique en microservices Spring.

---

## 3. Lancer l'infra

```bash
docker compose up -d          # Postgres + Redpanda
docker compose ps             # vérifier "healthy"
```

Puis chaque service depuis l'IDE (ou `mvn -pl <module> spring-boot:run`). Ports : 8080 / 8081 / 8082 / 8083.

**Depuis ton host**, les services joignent l'infra en `localhost` :
Postgres `localhost:5432`, Redpanda `localhost:19092`.
**Depuis un conteneur**, ce serait `postgres:5432` / `redpanda:9092` (nom de service, port interne).

Build : `mvn -q clean compile` (le squelette compile et boote ; les endpoints renvoient
`UnsupportedOperationException` tant que les TODO ne sont pas faits — c'est voulu).

---

## 4. Ce qui est fourni vs ce que tu codes

| Étape | Fourni (squelette) | Toi (TODO) |
|---|---|---|
| 1 — Fondations | Multi-module Maven, docker-compose (PG+Redpanda), 4 apps qui bootent, réseau/ADVERTISED expliqués | Vérifier que tout démarre et se voit |
| 2 — Sync + résilience | Interfaces Feign, config Resilience4j (CB/retry/timelimiter) commentée, ordre des aspects documenté | Implémenter appels + décorer l'appelant + tester open/half-open |
| 3 — SAGA + compensation | Entité `MissionSaga` (états), orchestrateur avec notes proxy/rollback | Enchaînement complet + compensation **idempotente** |
| 4 — Async Kafka | Producer + consumer squelette, config offsets/ack manuel | Publier après commit + consumer **dédupliqué** |
| 5 — Observabilité | `CorrelationIdFilter`, `FeignCorrelationConfig`, propagation Kafka documentée | Remplir MDC + propagation bout-en-bout + nettoyage `finally` |
| Stretch | `TelemetryIngestor` (pool+file bornés) | ExecutorService borné + backpressure |

---

## 5. Pièges & impacts cachés (rappel condensé — détails dans les commentaires du code)

- **Réseau Docker :** nom de service ≠ `localhost` ; port interne ≠ port mappé.
- **Kafka/Redpanda `ADVERTISED` :** 2 listeners (internal `redpanda:9092` / external `localhost:19092`), sinon bootstrap OK puis reconnexion cassée.
- **Starter tiers hors BOM Spring** (resilience4j, springdoc) → `ClassNotFound` : piloter par leur BOM/version explicite.
- **Proxy Spring** : `this.methode()` (self-invocation) et méthode `private` **contournent** `@Transactional`.
- **Rollback** : par défaut seulement sur `RuntimeException`/`Error` ; checked → `rollbackFor`.
- **Flush ≠ commit** (FlushMode AUTO) : le flush peut partir avant le commit.
- **Verrou pessimiste** (`SELECT … FOR UPDATE` / `@Lock`) : n'agit **que** dans une transaction ; borne le lock timeout, transaction courte, **jamais d'appel réseau lock tenu**.
- **Timeouts en cascade** : `retry × timeout` doit rester **< timeout de l'appelant**.
- **MDC + threads Tomcat recyclés** : nettoyer en `finally`, sinon fuite de correlation-id.
- **Idempotence** : compensations ET consumers doivent être rejouables (dedup par clé).
- **Mémoire** : Redpanda plafonné à 512M ; à faire sur ta machine, pas sur le serveur 911 Mo.

---

## 6. Livrable final (à générer À LA FIN, pas maintenant)

Fiche de révision par thème, format « pourquoi » d'oral + « ce qui casse » + phrase-réflexe,
comme tes docs exoplanètes/preditic. On la remplira une fois les étapes codées.
