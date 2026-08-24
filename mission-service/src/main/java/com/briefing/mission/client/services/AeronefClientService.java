package com.briefing.mission.client.services;

import com.briefing.mission.api.exceptions.AeronefIsNotAvailable;
import com.briefing.mission.client.AeronefClient;
import com.briefing.mission.client.aeronefDto.ReservationRequest;
import com.briefing.mission.client.aeronefDto.ReservationResponse;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AeronefClientService {

    private final AeronefClient aeronefClient;

    public AeronefClientService(AeronefClient aeronefClient) {
        this.aeronefClient = aeronefClient;
    }

    @TimeLimiter(name = "aeronef", fallbackMethod = "reserverFallback")
    public CompletableFuture<ReservationResponse> reserver(ReservationRequest req) {
        return CompletableFuture.supplyAsync(() -> aeronefClient.reserver(req));
    }

    public CompletableFuture<ReservationResponse> reserverFallback(ReservationRequest req, Throwable t) {
        throw new AeronefIsNotAvailable("Impossible to make reservation. Aeronef service timeout", t);
    }
}
