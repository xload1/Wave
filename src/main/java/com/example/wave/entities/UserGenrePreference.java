package com.example.wave.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_genre_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGenrePreference {

    @EmbeddedId
    private UserGenrePreferenceId id = new UserGenrePreferenceId();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("genreId")
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserGenrePreference(UserAccount user, Genre genre) {
        this.user = user;
        this.genre = genre;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}