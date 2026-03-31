package com.example.wave.DTOs.views;

import java.util.List;

public record TrackCatalogItemView(
        Long trackId,
        String title,
        Long artistId,
        String artistName,
        Integer popularity,
        List<String> genres
) {
}