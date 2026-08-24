package com.briefing.mission.api.exceptions;

public class AeronefIsNotAvailable extends RuntimeException {
    public AeronefIsNotAvailable(String message, Throwable cause) {
        super(message, cause);
    }
}
