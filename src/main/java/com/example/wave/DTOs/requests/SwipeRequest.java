package com.example.wave.DTOs.requests;

import com.example.wave.entities.SwipeReactionType;
import jakarta.validation.constraints.NotNull;

public record SwipeRequest(
        @NotNull Long targetUserId,
        @NotNull SwipeReactionType reactionType
) {
}
