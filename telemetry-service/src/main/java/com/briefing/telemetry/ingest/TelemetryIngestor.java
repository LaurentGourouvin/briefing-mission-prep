package com.briefing.telemetry.ingest;

import org.springframework.stereotype.Component;

/**
 * STRETCH - ingestion concurrente d'un flux de releves capteurs.
 *
 * Objectif : montrer que tu maitrises threading/backpressure, pas juste @Async.
 *
 *  - ExecutorService avec pool BORNE (pas newCachedThreadPool : il cree des
 *    threads sans limite -> OOM sous charge).
 *  - File BORNEE (ArrayBlockingQueue de capacite fixe) : c'est elle qui cree la
 *    BACKPRESSURE. File pleine + RejectedExecutionHandler = signal "ralentis".
 *  - Politique de rejet : CallerRunsPolicy fait executer la tache par le thread
 *    appelant -> ralentit naturellement le producteur (backpressure simple).
 *
 *  Footgun memoire : une file NON bornee (LinkedBlockingQueue par defaut) avale
 *  tout en RAM jusqu'a l'OOM au lieu de faire backpressure. Toujours borner.
 *
 *  Phrase-reflexe : "ingestion capteurs = rafales ; j'ai pris un pool borne +
 *   file bornee + CallerRuns ; le risque c'etait l'OOM par file infinie, je l'ai
 *   gere par la borne qui cree la backpressure."
 */
@Component
public class TelemetryIngestor {

    // TODO : construire un ThreadPoolExecutor(core, max, keepAlive,
    //        new ArrayBlockingQueue<>(capacite), new CallerRunsPolicy()).
    // TODO : submit(...) des releves ; gerer l'arret propre (shutdown + awaitTermination).

    public void ingerer(/* ReleveCapteur releve */) {
        throw new UnsupportedOperationException("TODO stretch : ingestion bornee + backpressure");
    }
}
