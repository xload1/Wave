package com.example.wave.repositories;

import com.example.wave.entities.UserAccount;
import com.example.wave.entities.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    boolean existsByFromUser_IdAndToUser_Id(Long fromUserId, Long toUserId);

    void deleteByFromUser_IdAndToUser_Id(Long fromUserId, Long toUserId);

    @Query("""
            select ul.toUser
            from UserLike ul
            where ul.fromUser.id = :userId
              and exists (
                  select 1
                  from UserLike back
                  where back.fromUser.id = ul.toUser.id
                    and back.toUser.id = :userId
              )
            order by ul.toUser.displayName asc, ul.toUser.id asc
            """)
    List<UserAccount> findMatchedUsers(@Param("userId") Long userId);
}
