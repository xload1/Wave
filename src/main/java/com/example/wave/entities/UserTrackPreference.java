package com.example.wave.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_track_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTrackPreference {

    @EmbeddedId
    private UserTrackPreferenceId id = new UserTrackPreferenceId();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("trackId")
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "preference_type", nullable = false, length = 20)
    private PreferenceType preferenceType;

    @Setter
    @Column(name = "weight", nullable = false, precision = 6, scale = 3)
    private BigDecimal weight = BigDecimal.ONE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserTrackPreference(UserAccount user, Track track, PreferenceType preferenceType, BigDecimal weight) {
        this.user = user;
        this.track = track;
        this.preferenceType = preferenceType;
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