package com.briefing.mission.client;

import com.briefing.mission.client.aeronefDto.ReservationRequest;
import com.briefing.mission.client.aeronefDto.ReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Appel SYNC mission -> aeronef (Etape 2).
 *
 * 'url' pointe vers le nom logique du service. En local hors Docker : localhost:8081.
 * Piege : le nom du @FeignClient doit etre unique ; il sert aussi de cle pour
 * la config Feign par-client.
 *
 * TODO : definis les 2 operations : reserver() et liberer() (compensation).
 *        Cote resilience, tu decoreras l'APPELANT (le service qui utilise ce
 *        client), pas l'interface Feign elle-meme (cf. MissionSagaOrchestrator).
 */
@FeignClient(name = "aeronef", url = "${clients.aeronef.url}")
public interface AeronefClient {

     @PostMapping("/aeronefs/reservations")
     ReservationResponse reserver(ReservationRequest req);
}
