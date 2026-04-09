package com.example.wave.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserAccount fromUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id", nullable = false)
    private UserAccount toUser;

    @Column(name = "message_ciphertext", nullable = false, columnDefinition = "text")
    private String messageCiphertext;

    @Column(name = "message_nonce", nullable = false, length = 128)
    private String messageNonce;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private Instant sentAt;

    public UserMessage(UserAccount fromUser, UserAccount toUser, String messageCiphertext, String messageNonce) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageCiphertext = messageCiphertext;
        this.messageNonce = messageNonce;
    }

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = Instant.now();
        }
    }
}
