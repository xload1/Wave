package com.example.wave.controllers;

import com.example.wave.DTOs.views.SpotifyArtistSearchView;
import com.example.wave.DTOs.views.SpotifyTrackView;
import com.example.wave.services.spotify.SpotifyCatalogService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spotify")
@RequiredArgsConstructor
@Validated
public class SpotifyController {

    private final SpotifyCatalogService spotifyCatalogService;

    @GetMapping("/search/tracks")
    public List<SpotifyTrackView> searchTracks(@RequestParam @NotBlank String query) {
        return spotifyCatalogService.searchTracks(query);
    }

    @GetMapping("/search/artists")
    public List<SpotifyArtistSearchView> searchArtists(@RequestParam @NotBlank String query) {
        return spotifyCatalogService.searchArtists(query);
    }
}
