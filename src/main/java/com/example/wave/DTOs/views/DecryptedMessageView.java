package com.example.wave.DTOs.views;

import java.time.Instant;

public record DecryptedMessageView(
        Long id,
        Long fromUserId,
        Long toUserId,
        String text,
        Instant sentAt
) {
}
