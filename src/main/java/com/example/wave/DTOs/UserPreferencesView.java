package com.example.wave.DTOs;

import java.util.List;

public record UserPreferencesView(
        Long userId,
        String username,
        List<TrackPreferenceView> trackPreferences,
        List<ArtistPreferenceView> artistPreferences,
        List<GenrePreferenceView> genrePreferences
) {
}
