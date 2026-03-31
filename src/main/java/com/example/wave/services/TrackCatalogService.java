package com.example.wave.services;

import com.example.wave.DTOs.views.TrackCatalogItemView;
import com.example.wave.repositories.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.wave.entities.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackCatalogService {

    private final TrackRepository trackRepository;

    public List<TrackCatalogItemView> getAllTracks() {
        return trackRepository.findAll(Sort.by(Sort.Direction.ASC, "title"))
                .stream()
                .map(track -> new TrackCatalogItemView(
                        track.getId(),
                        track.getTitle(),
                        track.getArtist().getId(),
                        track.getArtist().getName(),
                        track.getPopularity(),
                        track.getGenres()
                                .stream()
                                .map(Genre::getName)
                                .sorted(String.CASE_INSENSITIVE_ORDER)
                                .toList()
                ))
                .toList();
    }
}