package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.GenreStatResponse;
import com.ra.base_spring_boot.model.Genre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IGenreRepository extends JpaRepository<Genre, Long> {
    Page<Genre> findByGenreNameContainingIgnoreCase(String keyword, Pageable pageable);
    Optional<Genre> findByGenreName(String genreName);

    @Query("SELECT g.genreName, COUNT(s) " +
            "FROM Song s JOIN s.genres g " +
            "GROUP BY g.genreName " +
            "ORDER BY COUNT(s) DESC")
    List<Object[]> getGenreStatsBySong();

    @Query("SELECT g.genreName, SUM(s.views) " +
            "FROM Song s JOIN s.genres g " +
            "GROUP BY g.genreName " +
            "ORDER BY SUM(s.views) DESC")
    List<Object[]> getGenreStatsByView();
}
