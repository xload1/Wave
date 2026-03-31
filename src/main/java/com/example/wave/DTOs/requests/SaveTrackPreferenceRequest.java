package com.example.wave.DTOs.requests;

import com.example.wave.entities.PreferenceType;
import jakarta.validation.constraints.NotNull;

public record SaveTrackPreferenceRequest(
        @NotNull
        Long trackId,

        @NotNull
        PreferenceType preferenceType
) {
}
