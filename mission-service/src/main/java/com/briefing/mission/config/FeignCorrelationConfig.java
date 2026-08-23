package com.briefing.mission.config;

/**
 * Etape 5 - propager le correlation-id sur les appels Feign SORTANTS.
 *
 * TODO : declarer un bean RequestInterceptor qui recopie le MDC "correlationId"
 *        dans le header X-Correlation-Id de chaque requete Feign.
 *
 * Piege : ce RequestInterceptor s'execute sur le thread de l'appel Feign ; si
 * tu passes par un pool async / TimeLimiter (Resilience4j), le MDC n'est PAS
 * propage automatiquement au thread du pool -> il faut le transporter
 * explicitement (TtlMDC-like, ou capturer la valeur avant de basculer de thread).
 */
public class FeignCorrelationConfig {
    // TODO : @Bean RequestInterceptor correlationIdInterceptor() { ... }
}
