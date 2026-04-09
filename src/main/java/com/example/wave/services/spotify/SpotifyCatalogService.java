package com.example.wave.services.spotify;

import com.example.wave.DTOs.responses.SpotifyArtistSearchResponse;
import com.example.wave.DTOs.responses.SpotifyArtistTopTracksResponse;
import com.example.wave.DTOs.responses.SpotifyTrackSearchResponse;
import com.example.wave.DTOs.views.SpotifyArtistSearchView;
import com.example.wave.DTOs.views.SpotifyArtistView;
import com.example.wave.DTOs.views.SpotifyTrackView;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotifyCatalogService {

    private final SpotifyTokenService spotifyTokenService;
    private final ObjectMapper objectMapper;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.spotify.com/v1")
            .build();
    @Cacheable("SpotifyTracksSearch")
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
                .map(this::getSpotifyTrackView)
                .toList();
    }
    @Cacheable("spotifyTrackById")
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

        return getSpotifyTrackView(item);
    }

    private SpotifyTrackView getSpotifyTrackView(SpotifyTrackSearchResponse.Item item) {
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
    }
    @Cacheable("spotifyArtistsSearch")
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
                .map(this::getSpotifyArtistSearchView)
                .toList();
    }
    @Cacheable("spotifyArtistById")
    public SpotifyArtistSearchView getArtistBySpotifyId(String spotifyArtistId) {
        if (spotifyArtistId == null || spotifyArtistId.isBlank()) {
            throw new IllegalArgumentException("spotifyArtistId must not be blank");
        }

        String accessToken = spotifyTokenService.getAccessToken();

        SpotifyArtistSearchResponse.Item item = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/artists/{id}")
                        .build(spotifyArtistId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(SpotifyArtistSearchResponse.Item.class);

        if (item == null) {
            throw new EntityNotFoundException("Spotify artist not found: " + spotifyArtistId);
        }

        return getSpotifyArtistSearchView(item);
    }

    private SpotifyArtistSearchView getSpotifyArtistSearchView(SpotifyArtistSearchResponse.Item item) {
        String imageUrl = null;
        if (item.images() != null && !item.images().isEmpty()) {
            imageUrl = item.images().stream()
                    .filter(image -> image.width() != null && image.width() <= 300)
                    .findFirst()
                    .orElse(item.images().get(item.images().size() - 1))
                    .url();
        }

        return new SpotifyArtistSearchView(
                item.id(),
                item.name(),
                item.popularity(),
                item.external_urls() == null ? null : item.external_urls().spotify(),
                imageUrl
        );
    }

    public List<SpotifyTrackView> getArtistTopTracks(String spotifyArtistId) {
        if (spotifyArtistId == null || spotifyArtistId.isBlank()) {
            throw new IllegalArgumentException("spotifyArtistId must not be blank");
        }

        String accessToken = spotifyTokenService.getAccessToken();

        try {
            String body = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/artists/{id}/top-tracks")
                            .queryParam("market", "PL")
                            .build(spotifyArtistId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);

            if (body == null || body.isBlank()) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(body);
            JsonNode tracksNode = root.path("tracks");

            if (!tracksNode.isArray()) {
                return List.of();
            }

            List<SpotifyTrackView> result = new ArrayList<>();
            for (JsonNode trackNode : tracksNode) {
                result.add(mapTrackNodeToView(trackNode));
            }

            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load Spotify artist top tracks", ex);
        }
    }

    private SpotifyTrackView mapTrackNodeToView(JsonNode item) {
        String imageUrl = null;

        JsonNode images = item.path("album").path("images");
        if (images.isArray() && !images.isEmpty()) {
            JsonNode chosen = null;

            for (JsonNode image : images) {
                JsonNode widthNode = image.get("width");
                if (widthNode != null && !widthNode.isNull() && widthNode.asInt() <= 300) {
                    chosen = image;
                    break;
                }
            }

            if (chosen == null) {
                chosen = images.get(images.size() - 1);
            }

            JsonNode urlNode = chosen.get("url");
            if (urlNode != null && !urlNode.isNull()) {
                imageUrl = urlNode.asText();
            }
        }

        List<SpotifyArtistView> artists = new ArrayList<>();
        JsonNode artistsNode = item.path("artists");
        if (artistsNode.isArray()) {
            for (JsonNode artistNode : artistsNode) {
                artists.add(new SpotifyArtistView(
                        artistNode.path("id").asText(),
                        artistNode.path("name").asText()
                ));
            }
        }

        JsonNode externalUrls = item.path("external_urls");
        String spotifyUrl = externalUrls.isObject() && externalUrls.hasNonNull("spotify")
                ? externalUrls.get("spotify").asText()
                : null;

        return new SpotifyTrackView(
                item.path("id").asText(),
                item.path("name").asText(),
                item.path("popularity").asInt(),
                artists,
                spotifyUrl,
                imageUrl
        );
    }
}
