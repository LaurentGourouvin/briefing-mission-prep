package com.briefing.mission.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Appel SYNC mission -> clearance (Etape 2/3).
 * TODO : POST /clearances -> 200 (accordee) ou 4xx (refusee).
 *        Attention : "refus metier" != "erreur technique". Ne fais pas retry
 *        sur un refus metier (c'est deterministe, ca ne changera pas).
 */
@FeignClient(name = "clearance", url = "${clients.clearance.url}")
public interface ClearanceClient {
    // @PostMapping("/clearances")
    // ClearanceResponse demander(ClearanceRequest req);
}
