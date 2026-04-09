package com.example.wave.DTOs.views;

import java.util.List;

public record RecommendationFeedView(
        boolean ready,
        String message,
        List<RecommendationCardView> recommendations
) {
}
