package com.briefing.mission.api;

import com.briefing.mission.client.AeronefClient;
import com.briefing.mission.client.aeronefDto.ReservationRequest;
import com.briefing.mission.client.aeronefDto.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
/*
#########################################
        CONTROLLER A SUPPRIMER
#########################################
 */
@RestController
@RequestMapping("/test")
public class TestController {
    private final AeronefClient aeronefClient;

    public TestController(AeronefClient aeronefClient) {
        this.aeronefClient = aeronefClient;
    }

    @PostMapping("/reserver")
    public ResponseEntity<ReservationResponse> reserver(@RequestBody ReservationRequest req) {
        ReservationResponse res = aeronefClient.reserver(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(res.reservationId())
                .toUri();

        return ResponseEntity.created(location).body(res);
    }
}
