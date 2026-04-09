package com.example.wave.DTOs.requests;

public record UpdateProfileRequest(
        String displayName,
        String description
) {
}