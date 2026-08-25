package com.briefing.clearance.api;

import com.briefing.clearance.api.dto.ClearanceRequest;
import com.briefing.clearance.api.dto.ClearanceResponse;
import com.briefing.clearance.services.ClearanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clearances")
public class ClearanceController {

    private final ClearanceService clearanceService;

    public ClearanceController(ClearanceService clearanceService) {
        this.clearanceService = clearanceService;
    }

    @PostMapping
    public ResponseEntity<ClearanceResponse> demander(@Valid @RequestBody ClearanceRequest req) {
         ClearanceResponse result = this.clearanceService.validateClearance(req);
         return ResponseEntity.ok(result);
    }
}
