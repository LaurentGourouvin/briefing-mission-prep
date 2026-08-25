package com.briefing.mission.client.services;

import com.briefing.mission.client.ClearanceClient;
import com.briefing.mission.client.clearanceDto.ClearanceRequest;
import com.briefing.mission.client.clearanceDto.ClearanceResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ClearanceService {

    private final ClearanceClient clearanceClient;

    public ClearanceService(ClearanceClient clearanceClient) {
        this.clearanceClient = clearanceClient;
    }

    public ClearanceResponse askClearance(ClearanceRequest req) {
        return clearanceClient.askClearance(req);
    }
}
