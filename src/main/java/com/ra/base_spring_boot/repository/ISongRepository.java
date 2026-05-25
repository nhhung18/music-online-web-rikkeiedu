
package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ISongRepository extends JpaRepository<Song, Long> {
    List<Song> findByAlbumId(Long albumId);
    List<Song> findByArtistId(Long artistId);
    List<Song> findByGenres(Genre genre);
    Page<Song> findByTitleContainingIgnoreCase(Pageable pageable, String title);

    @Modifying
    @Query("UPDATE Song s SET s.views = COALESCE(s.views, 0L) + 1L WHERE s.id = :songId")
    void incrementView(@Param("songId") Long songId);

    // Kiểm tra bài hát có thuộc về artist không
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Song s WHERE s.id = :songId AND s.artist.id = :artistId")
    boolean existsBySongIdAndArtistId(@Param("songId") Long songId, @Param("artistId") Long artistId);

    // Query cho top 15 bài hát theo tuần
    @Query("SELECT s FROM Song s " +
            "WHERE s.createdAt >= :weekStart OR s.updatedAt >= :weekStart " +
            "ORDER BY COALESCE(s.views, 0L) DESC, s.createdAt DESC")
    List<Song> findTop15SongsByWeek(@Param("weekStart") LocalDateTime weekStart, Pageable pageable);

    List<Song> findTop15ByOrderByViewsDesc();

    // Query cho top bài hát của thời đại
    @Query("SELECT s FROM Song s " +
           "ORDER BY COALESCE(s.views, 0L) DESC, s.createdAt ASC")
    List<Song> findTopSongsAllTime(Pageable pageable);
    
    // Query cho trending tracks
    @Query("SELECT s FROM Song s " +
           "LEFT JOIN s.songHistories sh " +
           "WHERE sh.playedAt >= :trendingStart " +
           "GROUP BY s.id " +
           "HAVING COUNT(DISTINCT sh.id) > 0 " +
           "ORDER BY COUNT(DISTINCT sh.id) DESC, COALESCE(s.views, 0L) DESC")
    List<Song> findTrendingTracks(@Param("trendingStart") LocalDateTime trendingStart, Pageable pageable);

    // Query cho 15 bài hát mới nhất có lượt view cao (trong 7 ngày gần đây)
    @Query("SELECT s FROM Song s " +
            "WHERE s.createdAt >= :dateThreshold " +
            "ORDER BY COALESCE(s.views, 0L) DESC, s.createdAt DESC")
    List<Song> findNewSongsWithHighViews(@Param("dateThreshold") LocalDateTime dateThreshold, Pageable pageable);
}
