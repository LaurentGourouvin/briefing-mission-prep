package com.briefing.clearance.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ClearanceRequest(@NotBlank String codeMission, @NotBlank String aeronefId) {}
