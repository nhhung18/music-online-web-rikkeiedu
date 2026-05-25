package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.UserFavorSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IuserFavorSongRepository extends JpaRepository<UserFavorSong, Long> {
    Optional<UserFavorSong> findByUserIdAndSongId (Long userId, Long songId);
    List<UserFavorSong> findAllByUserId (Long userId);
    List<UserFavorSong> deleteAllByUserIdAndSongId (Long userId, Long songId);
}
