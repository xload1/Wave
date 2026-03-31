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

    @Setter
    @Column(name = "weight", nullable = false, precision = 6, scale = 3)
    private BigDecimal weight = BigDecimal.ONE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserArtistPreference(UserAccount user, Artist artist, BigDecimal weight) {
        this.user = user;
        this.artist = artist;
        this.weight = weight;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (weight == null) {
            weight = BigDecimal.ONE;
        }
    }
}
