package com.example.wave.controllers;

import com.example.wave.DTOs.requests.SendMessageRequest;
import com.example.wave.DTOs.views.DecryptedMessageView;
import com.example.wave.repositories.UserMessageRepository;
import com.example.wave.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/messages")
@RequiredArgsConstructor
public class MessageTest {
    private final MessageService messageService;
    private final UserMessageRepository userMessageRepository;

    @PostMapping("/send")
    public Long send(@RequestBody SendMessageRequest request) {
        return messageService.sendMessage(
                request.fromUserId(),
                request.toUserId(),
                request.text()
        );
    }

    @GetMapping("/conversation")
    public List<DecryptedMessageView> getConversation(
            @RequestParam Long firstUserId,
            @RequestParam Long secondUserId
    ) {
        return messageService.getConversation(firstUserId, secondUserId);
    }

    @DeleteMapping("/all")
    public void deleteAllMessages() {
        userMessageRepository.deleteAllInBatch();
    }
}
