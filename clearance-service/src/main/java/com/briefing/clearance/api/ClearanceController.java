package com.briefing.clearance.api;

import com.briefing.clearance.api.dto.ClearanceRequest;
import com.briefing.clearance.api.dto.ClearanceResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clearances")
public class ClearanceController {

    /**
     * Delivre ou refuse une clearance.
     *
     * DISTINCTION CLE (a expliquer, elle pilote la resilience cote mission) :
     *  - REFUS METIER (regle non satisfaite) -> reponse 4xx (ex 409/422) avec
     *    accordee=false. C'est DETERMINISTE : l'appelant NE DOIT PAS retry, il
     *    doit COMPENSER (liberer l'aeronef).
     *  - ERREUR TECHNIQUE (bug/indispo) -> 5xx. La, retry/circuit breaker ont
     *    du sens.
     *  Melanger les deux (renvoyer 500 pour un refus metier) casse la SAGA :
     *  mission croira a une panne et va ret/circuiter au lieu de compenser.
     *
     * TODO : implemente une regle de refus simple et deterministe (ex: certains
     *        types interdits, ou un creneau) pour pouvoir TESTER la compensation.
     */
    @PostMapping
    public ResponseEntity<ClearanceResponse> demander(@Valid @RequestBody ClearanceRequest req) {
        throw new UnsupportedOperationException("TODO Etape 2/3 : accorder/refuser (4xx metier vs 5xx technique)");
    }
}
