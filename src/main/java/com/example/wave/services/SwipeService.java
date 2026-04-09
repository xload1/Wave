package com.example.wave.services;

import com.example.wave.entities.SwipeReactionType;
import com.example.wave.entities.UserAccount;
import com.example.wave.entities.UserSwipe;
import com.example.wave.repositories.UserAccountRepository;
import com.example.wave.repositories.UserSwipeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SwipeService {

    private final UserSwipeRepository userSwipeRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    @CacheEvict(value = {"userRecommendations", "userMatches"}, allEntries = true)
    public boolean react(Long fromUserId, Long toUserId, SwipeReactionType reactionType) {
        validateIds(fromUserId, toUserId);

        if (reactionType == null) {
            throw new IllegalArgumentException("reactionType must not be null");
        }

        if (!userAccountRepository.existsById(fromUserId)) {
            throw new EntityNotFoundException("User not found: " + fromUserId);
        }
        if (!userAccountRepository.existsById(toUserId)) {
            throw new EntityNotFoundException("User not found: " + toUserId);
        }

        UserSwipe swipe = userSwipeRepository.findByFromUser_IdAndToUser_Id(fromUserId, toUserId)
                .orElseGet(() -> new UserSwipe(
                        userAccountRepository.getReferenceById(fromUserId),
                        userAccountRepository.getReferenceById(toUserId),
                        reactionType
                ));

        swipe.changeReactionType(reactionType);
        userSwipeRepository.save(swipe);

        return reactionType == SwipeReactionType.LIKE && isMatched(fromUserId, toUserId);
    }

    @Transactional
    @CacheEvict(value = {"userRecommendations", "userMatches"}, allEntries = true)
    public void clearReaction(Long fromUserId, Long toUserId) {
        validateIds(fromUserId, toUserId);

        userSwipeRepository.findByFromUser_IdAndToUser_Id(fromUserId, toUserId)
                .ifPresent(userSwipeRepository::delete);
    }

    public boolean isMatched(Long firstUserId, Long secondUserId) {
        validateIds(firstUserId, secondUserId);

        return userSwipeRepository.existsByFromUser_IdAndToUser_IdAndReactionType(
                firstUserId, secondUserId, SwipeReactionType.LIKE)
                && userSwipeRepository.existsByFromUser_IdAndToUser_IdAndReactionType(
                secondUserId, firstUserId, SwipeReactionType.LIKE);
    }

    @Cacheable("userMatches")
    public List<UserAccount> getMatches(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        if (!userAccountRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }

        return userSwipeRepository.findMatchedUsers(userId);
    }

    public Set<Long> getSwipedUserIds(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        return new HashSet<>(userSwipeRepository.findTargetUserIdsSwipedBy(userId));
    }

    private void validateIds(Long fromUserId, Long toUserId) {
        if (fromUserId == null) {
            throw new IllegalArgumentException("fromUserId must not be null");
        }
        if (toUserId == null) {
            throw new IllegalArgumentException("toUserId must not be null");
        }
        if (Objects.equals(fromUserId, toUserId)) {
            throw new IllegalArgumentException("User cannot react to self");
        }
    }
}
