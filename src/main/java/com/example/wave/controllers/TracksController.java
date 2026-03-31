package com.example.wave.controllers;

import com.example.wave.DTOs.views.TrackCatalogItemView;
import com.example.wave.services.TrackCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TracksController {

    private final TrackCatalogService trackCatalogService;

    @GetMapping
    public List<TrackCatalogItemView> getTracks() {
        return trackCatalogService.getAllTracks();
    }
}