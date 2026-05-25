package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.FormGenre;
import com.ra.base_spring_boot.dto.resp.GenreResp;
import com.ra.base_spring_boot.dto.resp.GenreStatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IGenreService {
    void create(FormGenre formGenre, MultipartFile file) throws Exception;

    Page<GenreResp> getAll(String keyword, Pageable pageable);

    GenreResp getGenre(Long id);

    void update(Long id, FormGenre formGenre);

    void delete(Long id);

    void uploadGenre(Long id, MultipartFile file) throws Exception;
    List<GenreStatResponse> statBySong();
    List<GenreStatResponse> statByView();

}
