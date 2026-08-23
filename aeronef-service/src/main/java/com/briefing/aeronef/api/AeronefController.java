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

    /**
     * Reserve un aeronef disponible du bon type.
     * TODO : transaction courte + verrou (optimiste OU pessimiste, justifie ton choix),
     *        idempotence via cleIdempotence (2 appels identiques -> meme reservation).
     */
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> reserver(@Valid @RequestBody ReservationRequest req) {
        ReservationResponse reservation = aeronefService.reserver(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservation.reservationId())
                .toUri();
        return ResponseEntity.created(location).body(reservation);
    }

    /**
     * Compensation : libere une reservation. DOIT etre IDEMPOTENTE :
     * liberer 2x, ou liberer une reservation inconnue -> 200/204, jamais 500.
     */
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> liberer(@PathVariable String id) {
        throw new UnsupportedOperationException("TODO Etape 3 : liberer (idempotent)");
    }
}
