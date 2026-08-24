package com.briefing.mission.api;

import com.briefing.mission.api.exceptions.AeronefIsNotAvailable;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class FeignErrorHandler {

    @ExceptionHandler(FeignException.class)
    public ProblemDetail handleFeignException(FeignException e) {
        if (e.status() == -1) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "Service Aeronef unavailable");
        }

        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(e.status()), e.contentUTF8());
    }

    @ExceptionHandler(AeronefIsNotAvailable.class)
    public ProblemDetail handleAeronefUnavailable(AeronefIsNotAvailable e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                e.getMessage());
    }

    @ExceptionHandler(TimeoutException.class)
    public ProblemDetail handleTimeout(TimeoutException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "Service aéronef indisponible (timeout)");
    }
}
