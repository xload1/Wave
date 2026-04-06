package com.example.wave.DTOs.views;

import java.util.List;

public record SpotifyTrackView(
        String spotifyTrackId,
        String title,
        Integer popularity,
        List<SpotifyArtistView> artists,
        String spotifyUrl,
        String imageUrl
) {
}
