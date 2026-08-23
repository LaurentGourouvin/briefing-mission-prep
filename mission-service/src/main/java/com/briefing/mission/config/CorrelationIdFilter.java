package com.briefing.mission.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Etape 5 - propagation du correlation-id (tu as deja fait le mono-service).
 *
 * Chaine visee : header HTTP entrant -> MDC -> (header Feign sortant) ->
 *                (header Kafka) -> MDC cote consumer.
 *
 * PIEGE CRITIQUE (Tomcat) : les threads sont RECYCLES d'une requete a l'autre.
 * Si tu oublies MDC.remove/clear en finally, la requete SUIVANTE herite du
 * correlation-id de la precedente -> logs pollues, correlation fausse.
 * => toujours nettoyer le MDC dans un finally.
 *
 * ORDRE des filtres : place-le TOT (Ordered.HIGHEST_PRECEDENCE) pour que tous
 * les logs en aval l'aient. Si un filtre de securite tourne avant et logue,
 * il n'aura pas encore le correlation-id -> reflechir a l'ordre.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {

    public static final String HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) req;
        // TODO : lire le header ; s'il est absent, en GENERER un (UUID).
        // TODO : MDC.put(MDC_KEY, id);
        try {
            chain.doFilter(req, res);
        } finally {
            // TODO : MDC.remove(MDC_KEY);  <-- NE PAS OUBLIER (thread recycle)
        }
    }
}
