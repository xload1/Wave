package com.example.wave.repositories;

import com.example.wave.entities.PreferenceType;
import com.example.wave.entities.UserTrackPreference;
import com.example.wave.entities.UserTrackPreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTrackPreferenceRepository extends JpaRepository<UserTrackPreference, UserTrackPreferenceId> {

    List<UserTrackPreference> findAllByUser_Id(Long userId);

    List<UserTrackPreference> findByUserIdAndPreferenceType(Long userId, PreferenceType preferenceType);

    List<UserTrackPreference> findByTrackId(Long trackId);

    boolean existsByUserIdAndTrackId(Long userId, Long trackId);

    void deleteByUserIdAndTrackId(Long userId, Long trackId);
    long countByUser_Id(Long userId);
}
