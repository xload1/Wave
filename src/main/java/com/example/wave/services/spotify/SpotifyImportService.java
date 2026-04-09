package com.example.wave.services.spotify;

import com.example.wave.DTOs.views.SpotifyArtistSearchView;
import com.example.wave.DTOs.views.SpotifyArtistView;
import com.example.wave.DTOs.views.SpotifyTrackView;
import com.example.wave.entities.Artist;
import com.example.wave.entities.PreferenceType;
import com.example.wave.entities.Track;
import com.example.wave.repositories.ArtistRepository;
import com.example.wave.repositories.TrackRepository;
import com.example.wave.services.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotifyImportService {

    private static final String SPOTIFY = "spotify";

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final SpotifyCatalogService spotifyCatalogService;
    private final PreferenceService preferenceService;

    @Transactional
    public Long ensureTrackImported(String spotifyTrackId) {
        return trackRepository.findByExternalSourceAndExternalId(SPOTIFY, spotifyTrackId)
                .map(Track::getId)
                .orElseGet(() -> {
                    SpotifyTrackView spotifyTrack = spotifyCatalogService.getTrackBySpotifyId(spotifyTrackId);

                    if (spotifyTrack.artists() == null || spotifyTrack.artists().isEmpty()) {
                        throw new IllegalStateException("Spotify track has no artists: " + spotifyTrackId);
                    }

                    SpotifyArtistView spotifyArtist = spotifyTrack.artists().get(0);

                    Artist artist = artistRepository.findByExternalSourceAndExternalId(SPOTIFY, spotifyArtist.spotifyArtistId())
                            .orElseGet(() -> artistRepository.save(
                                    new Artist(spotifyArtist.name(), SPOTIFY, spotifyArtist.spotifyArtistId())
                            ));

                    Track track = new Track(
                            spotifyTrack.title(),
                            artist,
                            spotifyTrack.popularity(),
                            SPOTIFY,
                            spotifyTrack.spotifyTrackId()
                    );

                    return trackRepository.save(track).getId();
                });
    }

    @Transactional
    public Long ensureArtistImported(String spotifyArtistId) {
        return artistRepository.findByExternalSourceAndExternalId(SPOTIFY, spotifyArtistId)
                .map(Artist::getId)
                .orElseGet(() -> {
                    SpotifyArtistSearchView spotifyArtist = spotifyCatalogService.getArtistBySpotifyId(spotifyArtistId);
                    Artist artist = new Artist(
                            spotifyArtist.name(),
                            SPOTIFY,
                            spotifyArtist.spotifyArtistId()
                    );
                    return artistRepository.save(artist).getId();
                });
    }

    @Transactional
    public void saveTrackPreferenceBySpotifyId(Long userId, String spotifyTrackId, PreferenceType preferenceType) {
        Long trackId = ensureTrackImported(spotifyTrackId);
        preferenceService.saveOrUpdateTrackPreference(userId, trackId, preferenceType);
    }

    @Transactional
    public void deleteTrackPreferenceBySpotifyId(Long userId, String spotifyTrackId) {
        trackRepository.findByExternalSourceAndExternalId(SPOTIFY, spotifyTrackId)
                .ifPresent(track -> preferenceService.deleteTrackPreference(userId, track.getId()));
    }

    @Transactional
    public void saveArtistPreferenceBySpotifyId(Long userId, String spotifyArtistId) {
        Long artistId = ensureArtistImported(spotifyArtistId);
        preferenceService.saveArtistPreference(userId, artistId);
    }

    @Transactional
    public void deleteArtistPreferenceBySpotifyId(Long userId, String spotifyArtistId) {
        artistRepository.findByExternalSourceAndExternalId(SPOTIFY, spotifyArtistId)
                .ifPresent(artist -> preferenceService.deleteArtistPreference(userId, artist.getId()));
    }
}
