package com.example.wave.DTOs;

import com.example.wave.entities.PreferenceType;

import java.math.BigDecimal;

public record TrackPreferenceView(
        Long trackId,
        String trackTitle,
        Long artistId,
        String artistName,
        PreferenceType preferenceType
) {
}
