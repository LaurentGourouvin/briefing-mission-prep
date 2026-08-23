# Briefing — orchestration de préparation de mission (microservices)

**Briefing** est un système de préparation de mission de vol construit en microservices.
Planifier une mission déclenche une **SAGA orchestrée** : réserver un aéronef, obtenir une
autorisation de vol, puis provisionner la captation de télémétrie — avec compensation
automatique si une étape échoue.

Le domaine est volontairement resserré autour d'un cas concret pour illustrer des patterns
distribués de bout en bout : communication synchrone résiliente, orchestration SAGA avec
compensation, messagerie asynchrone idempotente, et traçabilité inter-services.

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
        │ réserve/libère   │  │ accorde/refuse     │
        │ (verrou pessim.) │  │ (4xx métier/5xx)   │
        └──────────────────┘  └────────────────────┘
                         │
              ASYNC (3)  │ publie « MissionPlanifiee »  (après commit local)
                         ▼
                   ┌───────────┐        ┌────────────────────┐
                   │  Redpanda │───────▶│ telemetry-service  │ :8083
                   │  (Kafka)  │        │ consumer idempotent│
                   └───────────┘        │ + ingestion capteurs
                                        └────────────────────┘

Persistance : une base PostgreSQL par service (mission_db / aeronef_db / clearance_db).
```

### Les services

- **mission-service** — orchestrateur. Expose `POST /missions` et pilote la SAGA de bout en
  bout. Détient l'état d'avancement de chaque mission et déclenche les compensations.
- **aeronef-service** — gère le parc d'aéronefs. Réserve un appareil disponible du type
  demandé, le libère en compensation. Point de contention concurrentielle (verrou).
- **clearance-service** — délivre ou refuse une autorisation de vol selon des règles métier.
  Son refus est une décision métier normale, distincte d'une panne technique.
- **telemetry-service** — consomme l'événement « mission planifiée » et provisionne
  l'ingestion du flux de relevés capteurs (traitement concurrent borné).

### Choix d'orchestration

La SAGA vit dans **mission-service**, en **orchestration** (un composant central appelle
explicitement chaque étape) plutôt qu'en chorégraphie. Ce choix rend le flux lisible et
la compensation facile à raisonner, au prix d'un couplage assumé vers l'orchestrateur.

**Synchrone vs asynchrone :**
- `mission → aeronef` et `mission → clearance` sont **synchrones** : leur réponse conditionne
  la suite immédiate de la SAGA.
- `mission → telemetry` est **asynchrone** via Kafka : le provisionnement télémétrie n'est
  pas sur le chemin critique de la réponse, on le découple pour ne pas coupler la latence
  client à un service secondaire.

**Flux nominal :** réserver aéronef (sync) → obtenir clearance (sync) → publier
`MissionPlanifiee` (async). Si la clearance est refusée, l'orchestrateur **compense** en
libérant l'aéronef réservé. Les compensations sont idempotentes et rejouables.

---

## 2. Stack & versions

| Composant | Version | Rôle |
|---|---|---|
| Java | **17** | Langage cible. |
| Spring Boot | **3.3.5** | Socle applicatif ; son BOM aligne le cœur Spring. |
| Spring Cloud | **2023.0.3** (Leyton) | OpenFeign (clients HTTP déclaratifs). Train compatible Boot 3.3.x. |
| Resilience4j | **2.2.0** | Timeout / retry / circuit breaker. Starter tiers, piloté par son propre BOM. |
| Spring Kafka | via BOM Boot | Producer / consumer, aligné par Spring Boot. |
| Redpanda | **v24.2.7** | Broker Kafka-compatible mono-binaire, léger (pas de JVM ni ZooKeeper). |
| PostgreSQL | **16** | Une base par service. |

La compatibilité **Spring Boot 3.3.x ⇄ Spring Cloud 2023.0.x** est stricte : un train
Spring Cloud désaligné casse l'autoconfiguration au démarrage. Resilience4j étant un
starter tiers hors du BOM Spring, sa version est fixée via son propre BOM pour éviter les
conflits de versions transitives.

---

## 3. Démarrage

Prérequis : JDK 17, Maven, Docker.

```bash
# 1. Infrastructure (PostgreSQL + Redpanda)
docker compose up -d
docker compose ps            # attendre le statut "healthy"

# 2. Build
mvn -q clean compile

# 3. Lancer chaque service (IDE, ou en ligne de commande)
mvn -pl mission-service   spring-boot:run
mvn -pl aeronef-service   spring-boot:run
mvn -pl clearance-service spring-boot:run
mvn -pl telemetry-service spring-boot:run
```

Ports : mission `8080`, aeronef `8081`, clearance `8082`, telemetry `8083`.
Santé : `GET /actuator/health` sur chaque service.

### Accès à l'infrastructure

Depuis l'hôte, les services joignent l'infra via `localhost` (PostgreSQL `localhost:5432`,
Redpanda `localhost:19092`). Depuis un conteneur, l'adressage se fait par nom de service et
port interne (`postgres:5432`, `redpanda:9092`) — le broker est configuré avec un listener
interne et un listener externe pour couvrir les deux cas.

### Exemple : planifier une mission

```bash
curl -i -X POST localhost:8080/missions \
  -H "Content-Type: application/json" \
  -d '{"codeMission":"M-001","typeAeronefDemande":"RAFALE_B","creneauDebut":"2026-01-01T09:00:00Z"}'
```

---

## 4. Patterns implémentés

**Communication synchrone résiliente.** Les appels inter-services passent par des clients
Feign décorés Resilience4j : timeout, retry (sur erreurs transitoires uniquement), circuit
breaker (fail-fast quand un service est indisponible, reprise progressive en half-open).
Les refus métier (4xx) ne sont jamais retentés — ils sont déterministes.

**Concurrence & verrouillage.** La réservation d'un aéronef s'appuie sur un verrou
pessimiste (`SELECT … FOR UPDATE`), adapté à une ressource rare et disputée où le rejeu
optimiste serait coûteux. Lecture verrouillée et écriture sont dans la même transaction ;
aucun appel réseau n'est effectué verrou tenu.

**SAGA & compensation.** Aucune transaction distribuée : la cohérence entre services est
obtenue par compensation applicative. L'état de chaque SAGA est persisté, et les
compensations sont idempotentes pour supporter les rejeux (retry, reprise après crash).

**Idempotence.** Les opérations d'écriture réservation/compensation et le consumer Kafka
sont idempotents : rejouer une opération produit le même résultat sans doublon ni erreur.
Garanti au niveau base (contraintes d'unicité) et absorbé côté application.

**Messagerie asynchrone.** Publication de `MissionPlanifiee` après commit local (pour
éviter le dual-write), consumer en at-least-once avec déduplication et ack manuel après
traitement.

**Observabilité.** Propagation d'un correlation-id de bout en bout : header HTTP → MDC →
header Kafka → MDC côté consumer, pour tracer une requête à travers tous les services.

---

## 5. Points de vigilance opérationnels

- **Réseau Docker** : nom de service ≠ `localhost` ; port interne ≠ port mappé.
- **Advertised listeners** (Kafka/Redpanda) : deux listeners (interne / externe), sinon le
  bootstrap réussit mais la reconnexion échoue.
- **Proxy Spring** : `@Transactional` (et le verrou qui en dépend) est contourné en silence
  par un appel interne `this.methode()` ou une méthode `private`.
- **Rollback** : par défaut sur `RuntimeException`/`Error` seulement ; les checked exceptions
  nécessitent `rollbackFor`.
- **Timeouts en cascade** : `retry × timeout` doit rester inférieur au timeout de l'appelant.
- **MDC & threads recyclés** : nettoyer le MDC en `finally` (threads Tomcat/listeners réutilisés).
- **Schéma en dev** : `ddl-auto: update` ajoute mais ne modifie pas l'existant ; un changement
  de type de colonne sur une base déjà créée passe inaperçu (repartir d'un volume vide en dev).
  En production, migrations versionnées (Flyway/Liquibase).
- **Empreinte mémoire** : Redpanda plafonné à 512 Mo pour tenir sur une machine de développement.

---

## 6. Structure du dépôt

```
briefing/
├── pom.xml                 # parent : BOM Spring Cloud + Resilience4j
├── docker-compose.yml      # PostgreSQL + Redpanda
├── infra/init-db.sql       # création des bases par service
├── mission-service/        # orchestrateur SAGA
├── aeronef-service/        # réservation / libération
├── clearance-service/      # autorisation de vol
└── telemetry-service/      # consumer + ingestion capteurs
```