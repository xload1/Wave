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
public class UserGenrePreferenceId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "genre_id")
    private Long genreId;
}