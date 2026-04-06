package com.example.wave.DTOs.responses;

public record SpotifyTokenResponse(
        String access_token,
        String token_type,
        Integer expires_in
) {
}
