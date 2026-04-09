package com.example.wave.DTOs.views;

public record RecommendationCardView(
        Long userId,
        int score,
        RecommendationCardPageView firstPage,
        RecommendationCardPageView secondPage,
        RecommendationCardPageView thirdPage
) {
}
