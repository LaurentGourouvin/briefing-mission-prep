package com.briefing.mission.client.aeronefDto;

import jakarta.validation.constraints.NotBlank;

public record ReservationRequest(@NotBlank String type, @NotBlank String codeMission) {}
