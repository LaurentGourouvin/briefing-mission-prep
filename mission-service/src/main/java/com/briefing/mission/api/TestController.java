package com.briefing.mission.api;

import com.briefing.mission.client.AeronefClient;
import com.briefing.mission.client.aeronefDto.ReservationRequest;
import com.briefing.mission.client.aeronefDto.ReservationResponse;
import com.briefing.mission.client.services.AeronefClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

/*
#########################################
        CONTROLLER A SUPPRIMER
#########################################
 */
@RestController
@RequestMapping("/test")
public class TestController {
    private final AeronefClientService aeronefClientService;

    public TestController(AeronefClientService aeronefClientService) {
        this.aeronefClientService = aeronefClientService;
    }

    @PostMapping("/reserver")
    public ResponseEntity<ReservationResponse> reserver(@RequestBody ReservationRequest req) {
        CompletableFuture<ReservationResponse> res = aeronefClientService.reserver(req);
        ReservationResponse response = res.join();
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.reservationId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
