package com.example.wave.DTOs.requests;

import jakarta.validation.constraints.NotNull;

public record SaveArtistPreferenceRequest(
        @NotNull
        Long artistId
) {
}
