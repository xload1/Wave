package com.example.wave.controllers;

import com.example.wave.DTOs.requests.SendMessageRequest;
import com.example.wave.DTOs.views.DecryptedMessageView;
import com.example.wave.DTOs.views.MatchView;
import com.example.wave.entities.UserAccount;
import com.example.wave.services.AuthService;
import com.example.wave.services.MessageService;
import com.example.wave.services.SwipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
@Validated
public class MatchController {

    private final SwipeService swipeService;
    private final MessageService messageService;
    private final AuthService authService;

    @GetMapping
    public List<MatchView> getMatches() {
        UserAccount currentUser = authService.getCurrentUser();

        return swipeService.getMatches(currentUser.getId()).stream()
                .map(user -> new MatchView(
                        user.getId(),
                        user.getDisplayName(),
                        user.getDescription()
                ))
                .toList();
    }

    @GetMapping("/{otherUserId}/messages")
    public List<DecryptedMessageView> getConversation(@PathVariable Long otherUserId) {
        UserAccount currentUser = authService.getCurrentUser();
        return messageService.getConversation(currentUser.getId(), otherUserId);
    }

    @PostMapping("/{otherUserId}/messages")
    public ResponseEntity<Long> sendMessage(
            @PathVariable Long otherUserId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        UserAccount currentUser = authService.getCurrentUser();
        Long messageId = messageService.sendMessage(currentUser.getId(), otherUserId, request.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(messageId);
    }
}
