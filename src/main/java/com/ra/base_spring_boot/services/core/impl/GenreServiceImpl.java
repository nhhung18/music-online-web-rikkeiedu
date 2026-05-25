package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.FormGenre;
import com.ra.base_spring_boot.dto.resp.CloudinaryResp;
import com.ra.base_spring_boot.dto.resp.GenreResp;
import com.ra.base_spring_boot.dto.resp.GenreStatResponse;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.repository.IGenreRepository;
import com.ra.base_spring_boot.services.core.IGenreService;
import com.ra.base_spring_boot.services.external.cloudinary.CloudinaryService;
import com.ra.base_spring_boot.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements IGenreService {

    private final IGenreRepository genreRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public void create(FormGenre formGenre, MultipartFile file) throws Exception {
        Genre genre = Genre.builder()
                .genreName(formGenre.getGenreName())
                .build();
        genreRepository.save(genre);
        if (file != null) {
            uploadGenre(genre.getId(), file);
        }
    }

    @Override
    public GenreResp getGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Genre not found"));

        return GenreResp.builder()
                .id(genre.getId())
                .genreName(genre.getGenreName())
                .genreUrl(genre.getGenreUrl())
                .build();
    }

    @Override
    public Page<GenreResp> getAll(String keyword, Pageable pageable) {
        Page<Genre> genres;
        if (keyword != null && !keyword.isEmpty()) {
            genres = genreRepository.findByGenreNameContainingIgnoreCase(keyword, pageable);
        } else {
            genres = genreRepository.findAll(pageable);
        }
        return genres.map(genre ->
                GenreResp.builder()
                        .id(genre.getId())
                        .genreName(genre.getGenreName())
                        .genreUrl(genre.getGenreUrl())
                        .build());
    }


    @Override
    public void update(Long id, FormGenre formGenre) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new HttpNotFound("Genre not found"));

        genre.setGenreName(formGenre.getGenreName());
        genreRepository.save(genre);
    }

    @Override
    public void delete(Long id) {
        genreRepository.deleteById(id);
    }

    @Override
    public void uploadGenre(Long id, MultipartFile file) throws Exception {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new Exception("Genre id does not exist!"));
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResp response = cloudinaryService.uploadGenre(file, fileName);
        genre.setGenreUrl(response.getUrl());
        genreRepository.save(genre);
    }

    @Override
    public List<GenreStatResponse> statBySong() {
        List<Object[]> results = genreRepository.getGenreStatsBySong();

        return results.stream().map(row ->
                        GenreStatResponse.builder()
                                .name((String) row[0])
                                .value(((Number) row[1]).longValue())
                                .build())
                .toList();
    }

    @Override
    public List<GenreStatResponse> statByView() {
        List<Object[]> results = genreRepository.getGenreStatsByView();

        return results.stream().map(row ->
                        GenreStatResponse.builder()
                                .name((String) row[0])
                                .value(((Number) row[1]).longValue())
                                .build())
                .toList();
    }

}
