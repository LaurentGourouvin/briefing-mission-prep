package com.briefing.aeronef.exceptions;

public class ReservationAlreadyExist extends RuntimeException {
    public ReservationAlreadyExist(String codeMission) {
        super("Reservation already exist with this code mission : " + codeMission);
    }
}
