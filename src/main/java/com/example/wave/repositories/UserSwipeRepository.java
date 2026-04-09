package com.example.wave.repositories;

import com.example.wave.entities.SwipeReactionType;
import com.example.wave.entities.UserAccount;
import com.example.wave.entities.UserSwipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSwipeRepository extends JpaRepository<UserSwipe, Long> {

    Optional<UserSwipe> findByFromUser_IdAndToUser_Id(Long fromUserId, Long toUserId);

    boolean existsByFromUser_IdAndToUser_IdAndReactionType(Long fromUserId, Long toUserId, SwipeReactionType reactionType);

    @Query("""
            select us.toUser
            from UserSwipe us
            where us.fromUser.id = :userId
              and us.reactionType = com.example.wave.entities.SwipeReactionType.LIKE
              and exists (
                  select 1
                  from UserSwipe back
                  where back.fromUser.id = us.toUser.id
                    and back.toUser.id = :userId
                    and back.reactionType = com.example.wave.entities.SwipeReactionType.LIKE
              )
            order by us.toUser.displayName asc, us.toUser.id asc
            """)
    List<UserAccount> findMatchedUsers(@Param("userId") Long userId);

    @Query("""
            select us.toUser.id
            from UserSwipe us
            where us.fromUser.id = :userId
            """)
    List<Long> findTargetUserIdsSwipedBy(@Param("userId") Long userId);
}
