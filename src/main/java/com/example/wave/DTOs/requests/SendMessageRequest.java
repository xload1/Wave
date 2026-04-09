package com.example.wave.DTOs.requests;

public record SendMessageRequest(
        Long fromUserId,
        Long toUserId,
        String text
) {
}
