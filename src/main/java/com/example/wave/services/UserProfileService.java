package com.example.wave.services;

import com.example.wave.DTOs.requests.UpdateProfileRequest;
import com.example.wave.DTOs.views.UserProfileView;
import com.example.wave.entities.UserAccount;
import com.example.wave.repositories.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserAccountRepository userAccountRepository;
    private final PreferenceService preferenceService;

    public UserProfileView getProfile(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        return toView(user);
    }

    @Transactional
    public UserProfileView updateProfile(Long userId, UpdateProfileRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        String displayName = request.displayName() == null ? null : request.displayName().trim();
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }

        String description = request.description();
        if (description != null) {
            description = description.trim();
            if (description.isBlank()) {
                description = null;
            }
        }

        user.changeDisplayName(displayName);
        user.changeDescription(description);

        return toView(user);
    }

    private UserProfileView toView(UserAccount user) {
        return new UserProfileView(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getDescription(),
                preferenceService.getUserPreferences(user.getId())
        );
    }
}
