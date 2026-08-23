package com.briefing.aeronef.exceptions;

public class AeronefNotAvailable extends RuntimeException {
    public AeronefNotAvailable(String type) {
        super("Aeronef " + type + " not available");
    }
}
