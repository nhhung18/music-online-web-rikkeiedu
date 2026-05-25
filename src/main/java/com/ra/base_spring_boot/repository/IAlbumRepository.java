
package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Album;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IAlbumRepository extends JpaRepository<Album, Long>  {
    List<Album> findAllByArtist_Id(Long artistId);
    Page<Album> findByTitleContainingIgnoreCase (Pageable pageable, String title);
    
    // Query cho album nổi bật (có nhiều bài hát và view cao)
    @Query("SELECT a FROM Album a LEFT JOIN a.songs s GROUP BY a.id ORDER BY COUNT(s) DESC")
    List<Album> findFeaturedAlbums (Pageable pageable);
    
    // Query cho top 15 album (tổng views)
    @Query("SELECT a FROM Album a " +
           "LEFT JOIN a.songs s " +
           "GROUP BY a.id " +
           "ORDER BY COALESCE(SUM(s.views), 0L) DESC, a.createdAt DESC")
    List<Album> findTop15Albums(Pageable pageable);

    @Query("SELECT a FROM Album a ORDER BY a.createdAt DESC")
    List<Album> findNewAlbumsThisWeek (Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.id = :albumId AND a.artist.id = :artistId")
    boolean existsByAlbumIdAndArtistId(@Param("albumId") Long albumId, @Param("artistId") Long artistId);

}



