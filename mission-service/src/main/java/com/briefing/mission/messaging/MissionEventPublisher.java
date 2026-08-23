package com.briefing.mission.messaging;

import org.springframework.stereotype.Component;

/**
 * Producer Kafka (Etape 4). Publie MissionPlanifiee sur le topic "mission.planifiee".
 *
 * A EXPLIQUER :
 *  - at-least-once vs at-most-once : cote producer, acks=all + retries => le
 *    message peut etre ecrit PLUSIEURS fois (donc le CONSUMER doit deduper).
 *  - cle de partition : mettre le missionId en cle garantit l'ORDRE par mission
 *    (meme cle -> meme partition -> ordre preserve).
 *  - correlation-id (Etape 5) : le poser en HEADER Kafka, pas dans le body,
 *    pour le propager au consumer sans polluer le contrat.
 */
@Component
public class MissionEventPublisher {

    // TODO : injecter KafkaTemplate<String, MissionPlanifiee> (ou String JSON).

    public void publierMissionPlanifiee(/* MissionPlanifiee event, String correlationId */) {
        // HINT : kafkaTemplate.send(record) ou send(topic, key, value) ;
        //        ajoute le header "X-Correlation-Id".
        //        Pense au moment d'appel : APRES commit local (cf. SAGA etape 4).
        throw new UnsupportedOperationException("TODO Etape 4 : publier sur Kafka");
    }
}
