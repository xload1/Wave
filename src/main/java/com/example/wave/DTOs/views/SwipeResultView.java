package com.example.wave.DTOs.views;

import com.example.wave.entities.SwipeReactionType;

public record SwipeResultView(
        Long targetUserId,
        SwipeReactionType reactionType,
        boolean matched
) {
}
