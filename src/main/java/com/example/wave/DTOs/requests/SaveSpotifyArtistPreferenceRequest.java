package com.example.wave.DTOs.requests;

import jakarta.validation.constraints.NotBlank;

public record SaveSpotifyArtistPreferenceRequest(
        @NotBlank String spotifyArtistId
) {
}
