package com.example.wave.DTOs.responses;

import java.util.List;

public record SpotifyArtistSearchResponse(
        Artists artists
) {
    public record Artists(
            List<Item> items
    ) {
    }

    public record Item(
            String id,
            String name,
            Integer popularity,
            ExternalUrls external_urls
    ) {
    }

    public record ExternalUrls(
            String spotify
    ) {
    }
}
