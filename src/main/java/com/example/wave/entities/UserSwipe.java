package com.example.wave.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_swipe")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSwipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserAccount fromUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id", nullable = false)
    private UserAccount toUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 20)
    private SwipeReactionType reactionType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserSwipe(UserAccount fromUser, UserAccount toUser, SwipeReactionType reactionType) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.reactionType = reactionType;
    }

    public void changeReactionType(SwipeReactionType reactionType) {
        this.reactionType = reactionType;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
