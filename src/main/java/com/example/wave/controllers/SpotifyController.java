package com.example.wave.controllers;

import com.example.wave.DTOs.requests.SaveSpotifyArtistPreferenceRequest;
import com.example.wave.DTOs.requests.SaveSpotifyTrackPreferenceRequest;
import com.example.wave.DTOs.views.SpotifyArtistSearchView;
import com.example.wave.DTOs.views.SpotifyTrackView;
import com.example.wave.entities.UserAccount;
import com.example.wave.services.AuthService;
import com.example.wave.services.spotify.SpotifyCatalogService;
import com.example.wave.services.spotify.SpotifyImportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
@Validated
public class SpotifyController {

    private final SpotifyCatalogService spotifyCatalogService;
    private final SpotifyImportService spotifyImportService;
    private final AuthService authService;

    @GetMapping("/search/tracks")
    public List<SpotifyTrackView> searchTracks(@RequestParam @NotBlank String query) {
        return spotifyCatalogService.searchTracks(query);
    }

    @GetMapping("/search/artists")
    public List<SpotifyArtistSearchView> searchArtists(@RequestParam @NotBlank String query) {
        return spotifyCatalogService.searchArtists(query);
    }

    @GetMapping("/tracks/{spotifyTrackId}")
    public SpotifyTrackView getTrackBySpotifyId(@PathVariable String spotifyTrackId) {
        return spotifyCatalogService.getTrackBySpotifyId(spotifyTrackId);
    }

    @GetMapping("/artists/{spotifyArtistId}")
    public SpotifyArtistSearchView getArtistBySpotifyId(@PathVariable String spotifyArtistId) {
        return spotifyCatalogService.getArtistBySpotifyId(spotifyArtistId);
    }

    @GetMapping("/artists/{spotifyArtistId}/top-tracks")
    public List<SpotifyTrackView> getArtistTopTracks(@PathVariable String spotifyArtistId) {
        return spotifyCatalogService.getArtistTopTracks(spotifyArtistId);
    }

    @PutMapping("/me/track-preferences")
    public ResponseEntity<Void> saveTrackPreference(@Valid @RequestBody SaveSpotifyTrackPreferenceRequest request) {
        UserAccount user = authService.getCurrentUser();
        spotifyImportService.saveTrackPreferenceBySpotifyId(
                user.getId(),
                request.spotifyTrackId(),
                request.preferenceType()
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/track-preferences/{spotifyTrackId}")
    public ResponseEntity<Void> deleteTrackPreference(@PathVariable String spotifyTrackId) {
        UserAccount user = authService.getCurrentUser();
        spotifyImportService.deleteTrackPreferenceBySpotifyId(user.getId(), spotifyTrackId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/artist-preferences")
    public ResponseEntity<Void> saveArtistPreference(@Valid @RequestBody SaveSpotifyArtistPreferenceRequest request) {
        UserAccount user = authService.getCurrentUser();
        spotifyImportService.saveArtistPreferenceBySpotifyId(user.getId(), request.spotifyArtistId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/artist-preferences/{spotifyArtistId}")
    public ResponseEntity<Void> deleteArtistPreference(@PathVariable String spotifyArtistId) {
        UserAccount user = authService.getCurrentUser();
        spotifyImportService.deleteArtistPreferenceBySpotifyId(user.getId(), spotifyArtistId);
        return ResponseEntity.noContent().build();
    }
}