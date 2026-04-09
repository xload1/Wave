package com.example.wave.repositories;

import com.example.wave.entities.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

    @Query("""
        select m
        from UserMessage m
        where (m.fromUser.id = :firstUserId and m.toUser.id = :secondUserId)
           or (m.fromUser.id = :secondUserId and m.toUser.id = :firstUserId)
        order by m.sentAt asc, m.id asc
        """)
    List<UserMessage> findConversation(
            @Param("firstUserId") Long firstUserId,
            @Param("secondUserId") Long secondUserId
    );
}
