package com.example.wave.services.spotify;

import com.example.wave.DTOs.responses.SpotifyTrackSearchResponse;
import com.example.wave.DTOs.views.SpotifyArtistView;
import com.example.wave.DTOs.views.SpotifyTrackView;
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
                .map(item -> new SpotifyTrackView(
                        item.id(),
                        item.name(),
                        item.popularity(),
                        item.artists() == null
                                ? List.of()
                                : item.artists().stream()
                                .map(artist -> new SpotifyArtistView(artist.id(), artist.name()))
                                .toList(),
                        item.external_urls() == null ? null : item.external_urls().spotify()
                ))
                .toList();
    }
}
