package com.example.wave.DTOs.requests;

import com.example.wave.entities.PreferenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveSpotifyTrackPreferenceRequest(
        @NotBlank String spotifyTrackId,
        @NotNull PreferenceType preferenceType
) {
}
