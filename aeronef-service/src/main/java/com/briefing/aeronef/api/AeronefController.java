package com.briefing.aeronef.api;

import com.briefing.aeronef.api.dto.ReservationRequest;
import com.briefing.aeronef.api.dto.ReservationResponse;
import com.briefing.aeronef.service.AeronefService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/aeronefs")
public class AeronefController {

    private final AeronefService aeronefService;

    public AeronefController(AeronefService aeronefService) {
        this.aeronefService = aeronefService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> reserver(@Valid @RequestBody ReservationRequest req) {
        ReservationResponse reservation = aeronefService.reserver(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservation.reservationId())
                .toUri();
        return ResponseEntity.created(location).body(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> freeReservation(@PathVariable Long id) {
        this.aeronefService.freeReservation(id);
        return ResponseEntity.noContent().build();
    }
}
