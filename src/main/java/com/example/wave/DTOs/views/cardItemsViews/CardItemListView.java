package com.example.wave.DTOs.views.cardItemsViews;

import java.util.List;

public record CardItemListView(
        ItemType itemType,
        List<CardItemView> itemList
) {
}
