package com.example.wave.DTOs.views;

public record UserProfileView(
        Long id,
        String displayName,
        String email,
        String description,
        UserPreferencesView preferences
) {
}