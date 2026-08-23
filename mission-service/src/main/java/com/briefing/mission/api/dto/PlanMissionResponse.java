package com.briefing.mission.api.dto;

public record PlanMissionResponse(
        String missionId,
        String statut,        // PLANIFIEE / REFUSEE / EN_ECHEC ...
        String aeronefId,     // null si non reserve / libere par compensation
        String clearanceId    // null si refusee
) {}
