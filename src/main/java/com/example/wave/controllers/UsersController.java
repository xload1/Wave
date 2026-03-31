package com.example.wave.controllers;

import com.example.wave.DTOs.requests.CreateUserRequest;
import com.example.wave.DTOs.requests.SaveArtistPreferenceRequest;
import com.example.wave.DTOs.requests.SaveGenrePreferenceRequest;
import com.example.wave.DTOs.requests.SaveTrackPreferenceRequest;
import com.example.wave.DTOs.responses.CreateUserResponse;
import com.example.wave.DTOs.views.UserPreferencesView;
import com.example.wave.services.PreferenceService;
import com.example.wave.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UsersController {

    private final UserService userService;
    private final PreferenceService preferenceService;

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/track-preferences")
    public ResponseEntity<Void> saveOrUpdateTrackPreference(
            @PathVariable Long id,
            @Valid @RequestBody SaveTrackPreferenceRequest request
    ) {
        preferenceService.saveOrUpdateTrackPreference(id, request.trackId(), request.preferenceType());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/artist-preferences")
    public ResponseEntity<Void> saveArtistPreference(
            @PathVariable Long id,
            @Valid @RequestBody SaveArtistPreferenceRequest request
    ) {
        preferenceService.saveArtistPreference(id, request.artistId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/genre-preferences")
    public ResponseEntity<Void> saveGenrePreference(
            @PathVariable Long id,
            @Valid @RequestBody SaveGenrePreferenceRequest request
    ) {
        preferenceService.saveGenrePreference(id, request.genreId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/preferences")
    public UserPreferencesView getUserPreferences(@PathVariable Long id) {
        return preferenceService.getUserPreferences(id);
    }
}