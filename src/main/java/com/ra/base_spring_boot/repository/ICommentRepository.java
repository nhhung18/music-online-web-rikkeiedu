
package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICommentRepository extends JpaRepository<Comment, Long> {
    
    @Query("SELECT c FROM Comment c WHERE c.song.id = :songId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findBySongIdAndParentIsNull(Pageable pageable,  @Param("songId") Long songId);
    
    @Query("SELECT c FROM Comment c WHERE c.song.id = :songId ORDER BY c.createdAt DESC")
    Page<Comment> findBySongId(Pageable pageable,@Param("songId") Long songId);
    
    Optional<Comment> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
