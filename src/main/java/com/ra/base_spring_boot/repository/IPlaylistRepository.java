package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Playlist;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByNameContainingIgnoreCase(String keyword);
    List<Playlist> findByUser(User user, Pageable pageable);
    @Query("SELECT p FROM Playlist p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR p.user.id = :userId")
    List<Playlist> searchPlaylists(@Param("keyword") String keyword,
                                   @Param("userId") Long userId);
}
