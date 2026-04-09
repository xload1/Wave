package com.example.wave.DTOs.responses;

public record CreateUserResponse(
        Long id,
        String displayName,
        String email
) {
}
