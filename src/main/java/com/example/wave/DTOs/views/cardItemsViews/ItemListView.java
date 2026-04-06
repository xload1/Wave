package com.example.wave.DTOs.views.cardItemsViews;

import java.util.List;

public record ItemListView(
        ItemType itemType,
        List<cardItemView> itemList
) {
}
