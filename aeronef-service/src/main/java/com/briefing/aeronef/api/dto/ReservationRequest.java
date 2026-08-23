package com.briefing.aeronef.api.dto;

import jakarta.validation.constraints.NotBlank;

/** cle = clef d'idempotence fournie par l'appelant (missionId/codeMission). */
public record ReservationRequest(@NotBlank String type, @NotBlank String codeMission) {}
