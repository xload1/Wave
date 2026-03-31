package com.example.wave.DTOs.views;

import com.example.wave.entities.PreferenceType;

public record TrackPreferenceView(
        Long trackId,
        String trackTitle,
        Long artistId,
        String artistName,
        PreferenceType preferenceType
) {
}
