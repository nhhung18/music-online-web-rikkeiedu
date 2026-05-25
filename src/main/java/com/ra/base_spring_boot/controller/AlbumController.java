
package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumReq;
import com.ra.base_spring_boot.dto.resp.AlbumResp;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.services.core.IAlbumService;
import com.ra.base_spring_boot.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/album")
@RequiredArgsConstructor
public class AlbumController {

    private final IAlbumService albumService;

    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> getAlbums(Pageable pageable, @RequestParam(required = false) String title) {
        Long artistId = SecurityUtils.getCurrentUserId();
        List<Album> albums = albumService.getAllByArtist(artistId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(albums)
                        .build()
        );
    }

    // Lấy album theo id
    @GetMapping("/{albumId}")
    public ResponseEntity<?> getAlbumById(@PathVariable Long albumId) {
        AlbumResp album = albumService.getAlbumById(albumId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(album)
                        .build()
        );
    }

    // lấy tất cả albums và có tìm kiếm theo title
    @GetMapping("/all")
    public ResponseEntity<?> getAllAlbums(
            Pageable pageable,
            @RequestParam(required = false) String title) {

        Page<AlbumResp> albums = albumService.getAllAlbums(pageable, title);

        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(albums)
                        .build()
        );
    }


    // Thêm mới album
    // @apiNote handle createAlbum with title, releaseDate, artistId, coverImage, type
    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createAlbum(@Valid @RequestBody AlbumReq album) {
        Long artistId = SecurityUtils.getCurrentUserId();
        Album album1 = albumService.createAlbum(artistId, album);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(album1)
                        .build()
        );
    }

    // Cập nhật album
    // @apiNote handle updateAlbum with title, releaseDate, artistId, coverImage, type
    // Cập nhật album với ảnh bìa
    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @PutMapping("/{albumId}/update")
    public ResponseEntity<?> updateAlbum(
            @PathVariable Long albumId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) MultipartFile coverImage) {

        Long artistId = SecurityUtils.getCurrentUserId();

        // Tạo AlbumReq từ các tham số
        AlbumReq albumReq = new AlbumReq();
        if (title != null) {
            albumReq.setTitle(title);
        }
        if (releaseDate != null) {
            albumReq.setReleaseDate(LocalDateTime.parse(releaseDate));
        }

        Album updatedAlbum = albumService.updateAlbum(artistId, albumId, albumReq, coverImage);

        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(updatedAlbum)
                        .build()
        );
    }

    // Xóa album
    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @DeleteMapping("/{albumId}/delete")
    public ResponseEntity<?> deleteAlbum(@PathVariable Long albumId) {
        Long artistId = SecurityUtils.getCurrentUserId();
        albumService.deleteAlbum(artistId, albumId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Delete successfully")
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{albumId}/report")
    public ResponseEntity<?> deleteAlbumReport(@PathVariable Long albumId) {
        albumService.deleteAlbumAndNotifyArtist(albumId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Delete successfully")
                        .build()
        );
    }

    @GetMapping("/{artistId}/artist")
    public ResponseEntity<?> getAlbumByArtist(@PathVariable Long artistId){
        List<Album> albums = albumService.getSongByAlbum(artistId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(albums)
                        .build()
        );
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedAlbums() {
        List<AlbumResp> albums = albumService.getFeaturedAlbums();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(albums)
                        .build()
        );
    }


    @GetMapping("/top15")
    public ResponseEntity<?> getTop15Albums() {
        List<AlbumResp> albums = albumService.getTop15Albums();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(albums)
                        .build()
        );
    }

    @GetMapping("/new-releases")
    public ResponseEntity<?> getNewAlbumsThisWeek() {
        List<AlbumResp> albums = albumService.getNewAlbumsThisWeek();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(albums)
                        .build()
        );
    }

}

