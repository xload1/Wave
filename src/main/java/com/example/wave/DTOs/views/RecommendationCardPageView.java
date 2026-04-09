package com.example.wave.DTOs.views;

import com.example.wave.DTOs.views.cardItemsViews.CardItemListView;

public record RecommendationCardPageView(
        String title,
        String description,
        CardItemListView similarities
) {
}