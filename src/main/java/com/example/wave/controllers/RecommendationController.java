package com.example.wave.controllers;

import com.example.wave.DTOs.requests.SwipeRequest;
import com.example.wave.DTOs.views.RecommendationCardPageView;
import com.example.wave.DTOs.views.RecommendationCardView;
import com.example.wave.DTOs.views.RecommendationFeedView;
import com.example.wave.DTOs.views.SwipeResultView;
import com.example.wave.DTOs.views.cardItemsViews.CardItemListView;
import com.example.wave.entities.UserAccount;
import com.example.wave.other.UserScore;
import com.example.wave.repositories.UserArtistPreferenceRepository;
import com.example.wave.repositories.UserTrackPreferenceRepository;
import com.example.wave.services.AuthService;
import com.example.wave.services.RecommendationService;
import com.example.wave.services.SwipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final SwipeService swipeService;
    private final UserTrackPreferenceRepository userTrackPreferenceRepository;
    private final UserArtistPreferenceRepository userArtistPreferenceRepository;
    private final AuthService authService;

    @GetMapping("/recommendations")
    public RecommendationFeedView getRecommendations() {
        UserAccount currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();

        long trackCount = userTrackPreferenceRepository.countByUser_Id(userId);
        long artistCount = userArtistPreferenceRepository.countByUser_Id(userId);

        boolean ready = trackCount >= 8 && artistCount >= 2;
        if (!ready) {
            return new RecommendationFeedView(
                    false,
                    buildReadinessMessage(trackCount, artistCount),
                    List.of()
            );
        }

        List<RecommendationCardView> cards = recommendationService.getRecommendationListAndValues(userId.intValue())
                .stream()
                .map(userScore -> toCard(userId, userScore))
                .toList();

        return new RecommendationFeedView(true, null, cards);
    }

    @PostMapping("/swipes")
    public SwipeResultView react(@Valid @RequestBody SwipeRequest request) {
        UserAccount currentUser = authService.getCurrentUser();

        boolean matched = swipeService.react(
                currentUser.getId(),
                request.targetUserId(),
                request.reactionType()
        );

        return new SwipeResultView(
                request.targetUserId(),
                request.reactionType(),
                matched
        );
    }

    private RecommendationCardView toCard(Long mainUserId, UserScore userScore) {
        UserAccount other = userScore.userAccount();
        List<CardItemListView> similarities = recommendationService.generateCardSimilarities(mainUserId, other.getId());

        CardItemListView first = similarities.size() > 0 ? similarities.get(0) : null;
        CardItemListView second = similarities.size() > 1 ? similarities.get(1) : null;
        CardItemListView third = similarities.size() > 2 ? similarities.get(2) : null;

        return new RecommendationCardView(
                other.getId(),
                userScore.score(),
                new RecommendationCardPageView(other.getDisplayName(), null, first),
                new RecommendationCardPageView("About", other.getDescription(), second),
                new RecommendationCardPageView("More music overlap", null, third)
        );
    }

    private String buildReadinessMessage(long trackCount, long artistCount) {
        return "For recommendations choose at least 8 tracks and 2 artists. Now selected: "
                + trackCount + " tracks and " + artistCount + " artists.";
    }
}