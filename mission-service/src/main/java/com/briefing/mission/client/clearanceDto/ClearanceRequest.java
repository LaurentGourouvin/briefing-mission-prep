package com.briefing.mission.client.clearanceDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ClearanceRequest(@NotBlank String codeMission, @NotBlank String aeronefId, @NotNull OffsetDateTime debutMission) {}
