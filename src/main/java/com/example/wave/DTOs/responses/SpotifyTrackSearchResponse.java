package com.example.wave.DTOs.responses;

import java.util.List;

public record SpotifyTrackSearchResponse(
        Tracks tracks
) {
    public record Tracks(
            List<Item> items
    ) {
    }

    public record Item(
            String id,
            String name,
            Integer popularity,
            List<Artist> artists,
            ExternalUrls external_urls,
            Album album
    ) {
    }

    public record Artist(
            String id,
            String name
    ) {
    }

    public record ExternalUrls(
            String spotify
    ) {
    }

    public record Album(
            List<Image> images
    ) {
    }

    public record Image(
            String url,
            Integer height,
            Integer width
    ) {
    }
}