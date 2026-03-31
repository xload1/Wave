package com.example.wave.DTOs.requests;

import jakarta.validation.constraints.NotNull;

public record SaveGenrePreferenceRequest(
        @NotNull
        Long genreId
) {
}