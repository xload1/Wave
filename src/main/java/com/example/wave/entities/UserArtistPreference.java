package com.example.wave.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_artist_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserArtistPreference {

    @EmbeddedId
    private UserArtistPreferenceId id = new UserArtistPreferenceId();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserArtistPreference(UserAccount user, Artist artist) {
        this.user = user;
        this.artist = artist;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
