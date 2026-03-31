package com.example.wave.repositories;

import com.example.wave.entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByExternalSourceAndExternalId(String externalSource, String externalId);

    List<Artist> findByNameContainingIgnoreCase(String name);
}
