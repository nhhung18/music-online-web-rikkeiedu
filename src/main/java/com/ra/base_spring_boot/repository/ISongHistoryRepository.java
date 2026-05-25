
package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.SongHistory;
import com.ra.base_spring_boot.model.constants.UserSongId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ISongHistoryRepository extends JpaRepository<SongHistory, UserSongId> {
    
    // Lấy danh sách bài hát vừa phát gần đây của một user cụ thể
    @Query("SELECT sh.song FROM SongHistory sh WHERE sh.user.id = :userId ORDER BY sh.playedAt DESC")
    List<Song> findRecentlyPlayedSongsByUser(@Param("userId") Long userId, Pageable pageable);

    // Lấy lịch sử phát nhạc của user với thông tin chi tiết
    @Query("SELECT sh FROM SongHistory sh WHERE sh.user.id = :userId ORDER BY sh.playedAt DESC")
    List<SongHistory> findHistoryByUser(@Param("userId") Long userId, Pageable pageable);
    
    // Kiểm tra xem user đã nghe bài hát này chưa
    boolean existsByUserIdAndSongId(Long userId, Long songId);
    
    // Xóa bài hát khỏi lịch sử của user
    void deleteByUserIdAndSongId(Long userId, Long songId);
    
    // Xóa toàn bộ lịch sử của user
    void deleteByUserId(Long userId);
}
