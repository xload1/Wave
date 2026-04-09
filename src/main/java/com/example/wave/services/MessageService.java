package com.example.wave.services;

import com.example.wave.DTOs.views.DecryptedMessageView;
import com.example.wave.entities.SwipeReactionType;
import com.example.wave.entities.UserAccount;
import com.example.wave.entities.UserMessage;
import com.example.wave.repositories.UserAccountRepository;
import com.example.wave.repositories.UserMessageRepository;
import com.example.wave.repositories.UserSwipeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final UserMessageRepository userMessageRepository;
    private final UserAccountRepository userAccountRepository;
    private final SwipeService swipeService;
    private final MessageEncryptionService messageEncryptionService;

    @Transactional
    @CacheEvict(value = "conversation", allEntries = true)
    public Long sendMessage(Long fromUserId, Long toUserId, String text) {
        validateSendInput(fromUserId, toUserId, text);

        check(fromUserId, toUserId);

        MessageEncryptionService.EncryptedMessage encrypted = messageEncryptionService.encrypt(text);

        UserAccount fromUser = userAccountRepository.getReferenceById(fromUserId);
        UserAccount toUser = userAccountRepository.getReferenceById(toUserId);

        UserMessage message = new UserMessage(
                fromUser,
                toUser,
                encrypted.ciphertext(),
                encrypted.nonce()
        );

        return userMessageRepository.save(message).getId();
    }

    private void check(Long fromUserId, Long toUserId) {
        if (!userAccountRepository.existsById(fromUserId)) {
            throw new EntityNotFoundException("User not found: " + fromUserId);
        }
        if (!userAccountRepository.existsById(toUserId)) {
            throw new EntityNotFoundException("User not found: " + toUserId);
        }

        if (!swipeService.isMatched(fromUserId, toUserId)) {
            throw new IllegalArgumentException("Users are not matched");
        }
    }
    @Cacheable("conversation")
    public List<DecryptedMessageView> getConversation(Long firstUserId, Long secondUserId) {
        if (firstUserId == null) {
            throw new IllegalArgumentException("firstUserId must not be null");
        }
        if (secondUserId == null) {
            throw new IllegalArgumentException("secondUserId must not be null");
        }

        check(firstUserId, secondUserId);

        return userMessageRepository.findConversation(firstUserId, secondUserId).stream()
                .map(message -> new DecryptedMessageView(
                        message.getId(),
                        message.getFromUser().getId(),
                        message.getToUser().getId(),
                        messageEncryptionService.decrypt(
                                message.getMessageCiphertext(),
                                message.getMessageNonce()
                        ),
                        message.getSentAt()
                ))
                .toList();
    }

    private void validateSendInput(Long fromUserId, Long toUserId, String text) {
        if (fromUserId == null) {
            throw new IllegalArgumentException("fromUserId must not be null");
        }
        if (toUserId == null) {
            throw new IllegalArgumentException("toUserId must not be null");
        }
        if (Objects.equals(fromUserId, toUserId)) {
            throw new IllegalArgumentException("User cannot message self");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
        if (text.length() > 5000) {
            throw new IllegalArgumentException("text is too long");
        }
    }
}
