package com.example.wave.services;

import com.example.wave.entities.UserAccount;
import com.example.wave.entities.UserLike;
import com.example.wave.repositories.UserAccountRepository;
import com.example.wave.repositories.UserLikeRepository;
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
public class LikeService {

    private final UserLikeRepository userLikeRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    @CacheEvict(value = "userMatches", allEntries = true)
    public boolean likeUser(Long fromUserId, Long toUserId) {
        validateIds(fromUserId, toUserId);

        if (!userAccountRepository.existsById(fromUserId)) {
            throw new EntityNotFoundException("User not found: " + fromUserId);
        }
        if (!userAccountRepository.existsById(toUserId)) {
            throw new EntityNotFoundException("User not found: " + toUserId);
        }

        if (!userLikeRepository.existsByFromUser_IdAndToUser_Id(fromUserId, toUserId)) {
            UserAccount fromUser = userAccountRepository.getReferenceById(fromUserId);
            UserAccount toUser = userAccountRepository.getReferenceById(toUserId);

            userLikeRepository.save(new UserLike(fromUser, toUser));
        }

        return userLikeRepository.existsByFromUser_IdAndToUser_Id(toUserId, fromUserId);
    }

    @Transactional
    @CacheEvict(value = "userMatches", allEntries = true)
    public void unlikeUser(Long fromUserId, Long toUserId) {
        validateIds(fromUserId, toUserId);
        userLikeRepository.deleteByFromUser_IdAndToUser_Id(fromUserId, toUserId);
    }
    public boolean isMatched(Long firstUserId, Long secondUserId) {
        validateIds(firstUserId, secondUserId);

        return userLikeRepository.existsByFromUser_IdAndToUser_Id(firstUserId, secondUserId)
                && userLikeRepository.existsByFromUser_IdAndToUser_Id(secondUserId, firstUserId);
    }
    @Cacheable("userMatches")
    public List<UserAccount> getMatches(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        if (!userAccountRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }

        return userLikeRepository.findMatchedUsers(userId);
    }

    private void validateIds(Long fromUserId, Long toUserId) {
        if (fromUserId == null) {
            throw new IllegalArgumentException("fromUserId must not be null");
        }
        if (toUserId == null) {
            throw new IllegalArgumentException("toUserId must not be null");
        }
        if (Objects.equals(fromUserId, toUserId)) {
            throw new IllegalArgumentException("User cannot like self");
        }
    }
}
