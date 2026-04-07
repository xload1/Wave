package com.example.wave.other;

import com.example.wave.entities.UserAccount;
import lombok.AllArgsConstructor;

public record UserScore (
        UserAccount userAccount,
        int score
) {
}
