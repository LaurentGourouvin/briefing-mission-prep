package com.briefing.mission.saga;

import com.briefing.mission.api.dto.PlanMissionRequest;
import com.briefing.mission.api.dto.PlanMissionResponse;
import org.springframework.stereotype.Service;

/**
 * Coeur de l'Etape 3. Enchaine :
 *   1) reserver aeronef      (SYNC, via AeronefClient + resilience)
 *   2) demander clearance    (SYNC, via ClearanceClient)
 *   3) publier MissionPlanifiee (ASYNC, via MissionEventPublisher)  [Etape 4]
 *
 * Si (2) refuse  -> COMPENSATION : liberer l'aeronef (etape 1 defaite).
 *
 * ==========================================================================
 *  A EXPLIQUER A L'ORAL (tes trous connus) :
 * ==========================================================================

 *  - rollbackFor : par defaut rollback SEULEMENT sur RuntimeException/Error.
 *    Une checked exception NE declenche PAS le rollback -> @Transactional(rollbackFor=...).
 *
 *  - Compensation IDEMPOTENTE et REJOUABLE : liberer un aeronef deja libere doit
 *    etre un no-op (pas une 2e liberation, pas une erreur). On rejoue en cas de
 *    crash/retry -> cle d'idempotence portee par la reservation.
 * ==========================================================================
 */
@Service
public class MissionSagaOrchestrator {

    // TODO : injecter AeronefClient, ClearanceClient, MissionEventPublisher,
    //        et le repository JPA de MissionSaga.

    public PlanMissionResponse planifier(PlanMissionRequest req) {
        // ETAPE 1 - persister une MissionSaga CREEE (etat initial durable).
        // TODO

        // ETAPE 2 - reserver l'aeronef (SYNC, resilient). En cas de panne
        //           TECHNIQUE (timeout/circuit) -> EN_ECHEC, pas de compensation
        //           a faire (rien n'a ete reserve). Distingue bien les cas.
        // TODO

        // ETAPE 3 - demander la clearance (SYNC).
        //   - refus METIER -> compenser (liberer aeronef) -> statut REFUSEE.
        //   - panne technique -> decider ta politique (compenser par prudence ?).
        // TODO

        // ETAPE 4 - publier MissionPlanifiee (ASYNC). Point subtil :
        //   publier APRES le commit local (sinon tu peux publier un event pour
        //   une SAGA dont le commit a echoue -> "dual write problem").
        //   Piste : @TransactionalEventListener(AFTER_COMMIT) ou pattern outbox.
        // TODO

        throw new UnsupportedOperationException("TODO Etape 3 : implementer la SAGA + compensation");
    }

    // TODO : private/public void compenser(MissionSaga saga) { ... }
    //        -> attention au piege proxy si tu la veux @Transactional.
}
