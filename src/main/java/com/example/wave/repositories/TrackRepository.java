package com.example.wave.repositories;

import com.example.wave.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {

    Optional<Track> findByExternalSourceAndExternalId(String externalSource, String externalId);

    List<Track> findByTitleContainingIgnoreCase(String title);

    List<Track> findByArtistId(Long artistId);
}
