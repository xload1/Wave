package com.example.wave.DTOs.views;

public record SpotifyArtistSearchView(
        String spotifyArtistId,
        String name,
        Integer popularity,
        String spotifyUrl,
        String imageUrl
) {
}
