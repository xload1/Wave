package com.example.wave.repositories;

import com.example.wave.entities.UserArtistPreference;
import com.example.wave.entities.UserArtistPreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserArtistPreferenceRepository extends JpaRepository<UserArtistPreference, UserArtistPreferenceId> {

    List<UserArtistPreference> findAllByUser_Id(Long userId);

    List<UserArtistPreference> findByArtistId(Long artistId);

    boolean existsByUserIdAndArtistId(Long userId, Long artistId);

    void deleteByUserIdAndArtistId(Long userId, Long artistId);
}
