package com.briefing.mission.client.services;

import com.briefing.mission.client.AeronefClient;
import com.briefing.mission.client.aeronefDto.ReservationRequest;
import com.briefing.mission.client.aeronefDto.ReservationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AeronefClientService {

    private final AeronefClient aeronefClient;
    private static final Logger log = LoggerFactory.getLogger(AeronefClientService.class);

    public AeronefClientService(AeronefClient aeronefClient) {
        this.aeronefClient = aeronefClient;
    }

    @TimeLimiter(name = "aeronef")
    @Retry(name = "aeronef")
    @CircuitBreaker(name = "aeronef")
    public CompletableFuture<ReservationResponse> reserver(ReservationRequest req) {
        return CompletableFuture.supplyAsync(() -> aeronefClient.reserver(req));
    }

}
