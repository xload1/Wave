package com.example.wave.repositories;

import com.example.wave.entities.UserGenrePreference;
import com.example.wave.entities.UserGenrePreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGenrePreferenceRepository extends JpaRepository<UserGenrePreference, UserGenrePreferenceId> {

    List<UserGenrePreference> findAllByUser_Id(Long userId);

    List<UserGenrePreference> findByGenreId(Long genreId);

    boolean existsByUserIdAndGenreId(Long userId, Long genreId);

    void deleteByUserIdAndGenreId(Long userId, Long genreId);
}
