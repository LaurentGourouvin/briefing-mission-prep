package com.briefing.aeronef.exceptions;

public class NotFoundResource extends RuntimeException {
    public NotFoundResource(String message) {
        super(message);
    }
}
