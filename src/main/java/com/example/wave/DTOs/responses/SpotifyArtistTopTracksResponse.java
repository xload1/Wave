package com.example.wave.DTOs.responses;

import java.util.List;

public record SpotifyArtistTopTracksResponse(
        List<SpotifyTrackSearchResponse.Item> tracks
) {
}
