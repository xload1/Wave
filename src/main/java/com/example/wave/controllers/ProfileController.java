package com.example.wave.controllers;

import com.example.wave.DTOs.requests.UpdateProfileRequest;
import com.example.wave.DTOs.views.UserProfileView;
import com.example.wave.entities.UserAccount;
import com.example.wave.services.AuthService;
import com.example.wave.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Validated
public class ProfileController {

    private final UserProfileService userProfileService;
    private final AuthService authService;

    @GetMapping
    public UserProfileView getProfile() {
        UserAccount user = authService.getCurrentUser();
        return userProfileService.getProfile(user.getId());
    }

    @PatchMapping
    public UserProfileView updateProfile(@RequestBody UpdateProfileRequest request) {
        UserAccount user = authService.getCurrentUser();
        return userProfileService.updateProfile(user.getId(), request);
    }
}
