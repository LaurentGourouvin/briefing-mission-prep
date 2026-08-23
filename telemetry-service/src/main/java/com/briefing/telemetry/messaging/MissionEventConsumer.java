package com.briefing.telemetry.messaging;

import org.springframework.stereotype.Component;

/**
 * Consumer Kafka de "mission.planifiee" (Etape 4).
 *
 * ==========================================================================
 *  A EXPLIQUER A L'ORAL :
 * ==========================================================================
 *  - AT-LEAST-ONCE (le defaut sain) : on ACK/commit l'offset APRES traitement
 *    reussi. Si le consumer plante A MI-TRAITEMENT (avant commit d'offset), au
 *    redemarrage Kafka RELIVRE le message -> il sera traite 2x.
 *    => le traitement DOIT etre IDEMPOTENT (dedup par cle metier).
 *
 *  - AT-MOST-ONCE : on commit l'offset AVANT de traiter -> si crash, message
 *    PERDU. Rarement voulu.
 *
 *  - OFFSETS : position de lecture par (topic, partition, group). enable-auto-commit
 *    = false + ack manuel/apres-traitement = maitrise du "quand je considere fini".
 *
 *  - "consumer plante a mi-traitement" : si tu as deja ecrit la moitie des effets
 *    de bord avant le crash, la redelivery rejoue tout -> d'ou l'idempotence
 *    (table de messages traites : INSERT de l'eventId en 1er, si deja present -> skip).
 * ==========================================================================
 */
@Component
public class MissionEventConsumer {

    // TODO : injecter le store de dedup (Set en memoire pour l'entrainement,
    //        ou table SQL pour du "vrai") + l'ingestion telemetrie.

    // @KafkaListener(topics = "mission.planifiee", groupId = "telemetry")
    public void onMissionPlanifiee(/* ConsumerRecord<String, MissionPlanifiee> record */) {
        // ETAPE A : lire l'eventId (cle d'idempotence) depuis la cle/header.
        // ETAPE B : si deja traite -> return (dedup). Sinon marquer traite.
        // ETAPE C : provisionner l'ingestion telemetrie pour cette mission.
        // ETAPE D : recuperer le correlation-id depuis le HEADER Kafka -> MDC
        //           (Etape 5), et le RETIRER du MDC en finally (thread du listener recycle).
        throw new UnsupportedOperationException("TODO Etape 4 : consumer idempotent");
    }
}
