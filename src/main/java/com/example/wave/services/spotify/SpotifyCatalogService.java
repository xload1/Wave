package com.example.wave.services.spotify;

import com.example.wave.DTOs.responses.SpotifyArtistSearchResponse;
import com.example.wave.DTOs.responses.SpotifyTrackSearchResponse;
import com.example.wave.DTOs.views.SpotifyArtistSearchView;
import com.example.wave.DTOs.views.SpotifyArtistView;
import com.example.wave.DTOs.views.SpotifyTrackView;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotifyCatalogService {

    private final SpotifyTokenService spotifyTokenService;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.spotify.com/v1")
            .build();

    public List<SpotifyTrackView> searchTracks(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query must not be blank");
        }

        String accessToken = spotifyTokenService.getAccessToken();

        SpotifyTrackSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", "track")
                        .queryParam("market", "PL")
                        .queryParam("limit", 10)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(SpotifyTrackSearchResponse.class);

        if (response == null || response.tracks() == null || response.tracks().items() == null) {
            return List.of();
        }



        return response.tracks().items().stream()
                .map(item -> {
                    // find first small image from all sizes
                    String imageUrl = null;
                    if (item.album() != null && item.album().images() != null && !item.album().images().isEmpty()) {
                        imageUrl = item.album().images().stream()
                                .filter(image -> image.width() != null && image.width() <= 300)
                                .findFirst()
                                .orElse(item.album().images().get(item.album().images().size() - 1))
                                .url();
                    }

                    return new SpotifyTrackView(
                        item.id(),
                        item.name(),
                        item.popularity(),
                        item.artists() == null
                                ? List.of()
                                : item.artists().stream()
                                .map(artist -> new SpotifyArtistView(artist.id(), artist.name()))
                                .toList(),
                        item.external_urls() == null ? null : item.external_urls().spotify(),
                        imageUrl
                );
                })
                .toList();
    }

    public SpotifyTrackView getTrackBySpotifyId(String spotifyTrackId) {
        if (spotifyTrackId == null || spotifyTrackId.isBlank()) {
            throw new IllegalArgumentException("spotifyTrackId must not be blank");
        }

        String accessToken = spotifyTokenService.getAccessToken();

        SpotifyTrackSearchResponse.Item item = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tracks/{id}")
                        .queryParam("market", "PL")
                        .build(spotifyTrackId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(SpotifyTrackSearchResponse.Item.class);

        if (item == null) {
            throw new EntityNotFoundException("Spotify track not found: " + spotifyTrackId);
        }

        String imageUrl = null;
        if (item.album() != null && item.album().images() != null && !item.album().images().isEmpty()) {
            imageUrl = item.album().images().stream()
                    .filter(image -> image.width() != null && image.width() <= 300)
                    .findFirst()
                    .orElse(item.album().images().get(item.album().images().size() - 1))
                    .url();
        }

        return new SpotifyTrackView(
                item.id(),
                item.name(),
                item.popularity(),
                item.artists() == null
                        ? List.of()
                        : item.artists().stream()
                        .map(artist -> new SpotifyArtistView(artist.id(), artist.name()))
                        .toList(),
                item.external_urls() == null ? null : item.external_urls().spotify(),
                imageUrl
        );
    }

    public List<SpotifyArtistSearchView> searchArtists(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query must not be blank");
        }

        String accessToken = spotifyTokenService.getAccessToken();

        SpotifyArtistSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", "artist")
                        .queryParam("limit", 10)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(SpotifyArtistSearchResponse.class);

        if (response == null || response.artists() == null || response.artists().items() == null) {
            return List.of();
        }

        return response.artists().items().stream()
                .map(item -> new SpotifyArtistSearchView(
                        item.id(),
                        item.name(),
                        item.popularity(),
                        item.external_urls() == null ? null : item.external_urls().spotify()
                ))
                .toList();
    }
}
