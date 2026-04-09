package com.example.wave.DTOs.views;

public record AuthUserView(
        Long id,
        String displayName,
        String email,
        String description
) {
}
