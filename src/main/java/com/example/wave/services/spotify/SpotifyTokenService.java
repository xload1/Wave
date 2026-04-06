package com.example.wave.services.spotify;

import com.example.wave.DTOs.responses.SpotifyTokenResponse;
import com.example.wave.other.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SpotifyTokenService {

    private final SpotifyProperties spotifyProperties;
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://accounts.spotify.com")
            .build();

    public String getAccessToken() {
        String credentials = spotifyProperties.clientId() + ":" + spotifyProperties.clientSecret();
        String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        SpotifyTokenResponse response = restClient.post()
                .uri("/api/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials")
                .retrieve()
                .body(SpotifyTokenResponse.class);

        if (response == null || response.access_token() == null || response.access_token().isBlank()) {
            throw new IllegalStateException("Failed to obtain Spotify access token");
        }

        return response.access_token();
    }
}
