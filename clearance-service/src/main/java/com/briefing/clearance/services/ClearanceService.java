package com.briefing.clearance.services;

import com.briefing.clearance.api.dto.ClearanceRequest;
import com.briefing.clearance.api.dto.ClearanceResponse;
import com.briefing.clearance.exceptions.ClearanceRejectException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClearanceService {
    public ClearanceResponse validateClearance(ClearanceRequest req) {
        int heure = req.debutMission().getHour();
        boolean estDeNuit = (heure >= 23) || (heure < 5);

        if (estDeNuit) {
            throw new ClearanceRejectException("Night flights are not permitted during this time slot");
        }

        return new ClearanceResponse(UUID.randomUUID().toString(), true, null);
    }
}
