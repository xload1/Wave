package com.example.wave.services;

import com.example.wave.DTOs.views.ArtistPreferenceView;
import com.example.wave.DTOs.views.GenrePreferenceView;
import com.example.wave.DTOs.views.TrackPreferenceView;
import com.example.wave.DTOs.views.UserPreferencesView;
import com.example.wave.entities.*;
import com.example.wave.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreferenceService {

    private final UserAccountRepository userAccountRepository;
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;

    private final UserTrackPreferenceRepository userTrackPreferenceRepository;
    private final UserArtistPreferenceRepository userArtistPreferenceRepository;
    private final UserGenrePreferenceRepository userGenrePreferenceRepository;

    @Transactional
    @CacheEvict(value = {"userRecommendations", "cardSimilarities"}, allEntries = true)
    public void saveOrUpdateTrackPreference(Long userId, Long trackId, PreferenceType preferenceType) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (trackId == null) {
            throw new IllegalArgumentException("trackId must not be null");
        }
        if (preferenceType == null) {
            throw new IllegalArgumentException("preferenceType must not be null");
        }

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found: " + trackId));

        UserTrackPreferenceId id = new UserTrackPreferenceId(userId, trackId);

        UserTrackPreference preference = userTrackPreferenceRepository.findById(id)
                .orElseGet(() -> new UserTrackPreference(user, track, preferenceType));

        preference.setPreferenceType(preferenceType);

        userTrackPreferenceRepository.save(preference);
    }

    @Transactional
    @CacheEvict(value = {"userRecommendations", "cardSimilarities"}, allEntries = true)
    public void saveArtistPreference(Long userId, Long artistId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (artistId == null) {
            throw new IllegalArgumentException("artistId must not be null");
        }

        if (!userAccountRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }

        if (!artistRepository.existsById(artistId)) {
            throw new EntityNotFoundException("Artist not found: " + artistId);
        }

        UserArtistPreferenceId id = new UserArtistPreferenceId(userId, artistId);

        if (userArtistPreferenceRepository.existsById(id)) {
            return;
        }

        UserAccount userRef = userAccountRepository.getReferenceById(userId);
        Artist artistRef = artistRepository.getReferenceById(artistId);

        userArtistPreferenceRepository.save(new UserArtistPreference(userRef, artistRef));
    }

    @Transactional
    public void saveGenrePreference(Long userId, Long genreId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (genreId == null) {
            throw new IllegalArgumentException("genreId must not be null");
        }

        if (!userAccountRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }

        if (!genreRepository.existsById(genreId)) {
            throw new EntityNotFoundException("Genre not found: " + genreId);
        }

        UserGenrePreferenceId id = new UserGenrePreferenceId(userId, genreId);

        if (userGenrePreferenceRepository.existsById(id)) {
            return;
        }

        UserAccount userRef = userAccountRepository.getReferenceById(userId);
        Genre genreRef = genreRepository.getReferenceById(genreId);

        userGenrePreferenceRepository.save(new UserGenrePreference(userRef, genreRef));
    }

    public UserPreferencesView getUserPreferences(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        List<TrackPreferenceView> trackPreferences = userTrackPreferenceRepository.findAllByUser_Id(userId)
                .stream()
                .sorted(
                        Comparator.comparing(
                                        (UserTrackPreference p) -> p.getPreferenceType() == PreferenceType.FAVORITE ? 0 : 1
                                )
                                .thenComparing(p -> p.getTrack().getTitle(), String.CASE_INSENSITIVE_ORDER)
                )
                .map(p -> new TrackPreferenceView(
                        p.getTrack().getId(),
                        p.getTrack().getTitle(),
                        p.getTrack().getArtist().getId(),
                        p.getTrack().getArtist().getName(),
                        p.getPreferenceType()
                ))
                .toList();

        List<ArtistPreferenceView> artistPreferences = userArtistPreferenceRepository.findAllByUser_Id(userId)
                .stream()
                .sorted(Comparator.comparing(p -> p.getArtist().getName(), String.CASE_INSENSITIVE_ORDER))
                .map(p -> new ArtistPreferenceView(
                        p.getArtist().getId(),
                        p.getArtist().getName()
                ))
                .toList();

        List<GenrePreferenceView> genrePreferences = userGenrePreferenceRepository.findAllByUser_Id(userId)
                .stream()
                .sorted(Comparator.comparing(p -> p.getGenre().getName(), String.CASE_INSENSITIVE_ORDER))
                .map(p -> new GenrePreferenceView(
                        p.getGenre().getId(),
                        p.getGenre().getName()
                ))
                .toList();

        return new UserPreferencesView(
                user.getId(),
                user.getUsername(),
                trackPreferences,
                artistPreferences,
                genrePreferences
        );
    }

    @Transactional
    public void deleteTrackPreference(Long userId, Long trackId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (trackId == null) {
            throw new IllegalArgumentException("trackId must not be null");
        }

        UserTrackPreferenceId id = new UserTrackPreferenceId(userId, trackId);

        if (!userTrackPreferenceRepository.existsById(id)) return;

        userTrackPreferenceRepository.deleteById(id);
    }

    @Transactional
    public void deleteArtistPreference(Long userId, Long artistId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (artistId == null) {
            throw new IllegalArgumentException("artistId must not be null");
        }

        UserArtistPreferenceId id = new UserArtistPreferenceId(userId, artistId);

        if (!userArtistPreferenceRepository.existsById(id)) return;

        userArtistPreferenceRepository.deleteById(id);
    }
}
