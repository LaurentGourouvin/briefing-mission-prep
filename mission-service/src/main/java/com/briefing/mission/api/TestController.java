package com.briefing.mission.api;

import com.briefing.mission.client.aeronefDto.ReservationRequest;
import com.briefing.mission.client.aeronefDto.ReservationResponse;
import com.briefing.mission.client.clearanceDto.ClearanceRequest;
import com.briefing.mission.client.clearanceDto.ClearanceResponse;
import com.briefing.mission.client.services.AeronefClientService;
import com.briefing.mission.client.services.ClearanceService;
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
    private final ClearanceService clearanceService;

    public TestController(AeronefClientService aeronefClientService, ClearanceService clearanceService) {
        this.aeronefClientService = aeronefClientService;
        this.clearanceService = clearanceService;
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

    @PostMapping("/clearances")
    public ResponseEntity<ClearanceResponse> askClearance(@RequestBody ClearanceRequest req) {
        ClearanceResponse res = clearanceService.askClearance(req);
        return ResponseEntity.ok(res);
    }
}
