package com.briefing.mission.api;

import com.briefing.mission.api.dto.PlanMissionRequest;
import com.briefing.mission.api.dto.PlanMissionResponse;
import com.briefing.mission.saga.MissionSagaOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/missions")
public class MissionController {

    private final MissionSagaOrchestrator saga;

    public MissionController(MissionSagaOrchestrator saga) {
        this.saga = saga;
    }

    /**
     * Declenche la SAGA orchestree.
     *
     * Point d'oral (Etape 3) : ce endpoint N'EST PAS @Transactional distribue.
     * Il n'existe aucun rollback ACID qui traverse aeronef-service et
     * clearance-service. La coherence est atteinte par COMPENSATION applicative.
     *
     * TODO : appeler saga.planifier(request), mapper le resultat en reponse,
     *        choisir le bon code HTTP (201 si planifiee, 409/422 si clearance refusee...).
     */
    @PostMapping
    public ResponseEntity<PlanMissionResponse> planifier(@Valid @RequestBody PlanMissionRequest request) {
        // HINT : distingue "refus metier" (clearance dit non -> compensation OK, 409/422)
        //        de "panne technique" (circuit ouvert, timeout -> 503).
        throw new UnsupportedOperationException("TODO Etape 3 : brancher la SAGA");
    }
}
