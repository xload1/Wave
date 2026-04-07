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
            ExternalUrls external_urls,
            List<Image> images
    ) {
    }

    public record ExternalUrls(
            String spotify
    ) {
    }

    public record Image(
            String url,
            Integer height,
            Integer width
    ) {
    }
}
