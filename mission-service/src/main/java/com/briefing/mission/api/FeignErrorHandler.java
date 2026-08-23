package com.briefing.mission.api;

import feign.FeignException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FeignErrorHandler {

    @ExceptionHandler(FeignException.class)
    public ProblemDetail handleFeignException(FeignException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(e.status()), e.contentUTF8());
    }
}
