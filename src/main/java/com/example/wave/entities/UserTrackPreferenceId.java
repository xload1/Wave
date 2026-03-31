package com.example.wave.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserTrackPreferenceId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "track_id")
    private Long trackId;
}