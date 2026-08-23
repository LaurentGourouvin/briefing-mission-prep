package com.briefing.mission.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

/**
 * Requete d'entree de POST /missions.
 * TODO : ajoute les contraintes de validation qui ont du sens metier.
 */
public record PlanMissionRequest(
        @NotBlank String codeMission,
        @NotBlank String typeAeronefDemande,
        @NotNull OffsetDateTime creneauDebut
) {}
