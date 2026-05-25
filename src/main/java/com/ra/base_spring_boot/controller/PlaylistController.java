package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import com.ra.base_spring_boot.services.core.IPlaylistService;
import com.ra.base_spring_boot.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final IPlaylistService playlistService;

    @GetMapping("/search")
    public ResponseEntity<?> searchPlaylists(@RequestParam(required = false) String keyword) {
        List<PlaylistResp> playlists = playlistService.searchPlaylists(keyword);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(playlists)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<?> getPlayLists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        String sortField = sort[0];
        Sort.Direction sortDirection = sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        List<PlaylistResp> playlists = playlistService.getPlaylists(pageable);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(playlists)
                        .build()
        );
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<?> getPlaylistById(@PathVariable Long playlistId){
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(playlistService.getPlaylistById(playlistId))
                        .build()
        );
    }

    // Tạo mới playlist
    @PostMapping
    public ResponseEntity<?> createPlaylist(@RequestBody PlaylistReq request) {
        Long userId = SecurityUtils.getCurrentUserId();
        PlaylistResp playlist = playlistService.createPlaylist(userId, request);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(playlist)
                        .build()
        );
    }

    // Thêm bài hát vào playlist
    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<?> addSongToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        Long userId = SecurityUtils.getCurrentUserId();
        PlaylistResp playlist = playlistService.addSongToPlaylist(userId, playlistId, songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(playlist)
                        .build()
        );
    }


    // Cập nhật thông tin playlist
    @PutMapping("/{playlistId}")
    public ResponseEntity<?> updatePlaylist(
            @PathVariable Long playlistId,
            @RequestBody PlaylistReq request) {
        Long userId = SecurityUtils.getCurrentUserId();
        PlaylistResp playlist = playlistService.updatePlaylist(userId, playlistId, request);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(playlist)
                        .build()
        );
    }

    // Xóa playlist
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long playlistId) {
        Long userId = SecurityUtils.getCurrentUserId();
        playlistService.deletePlaylistById(userId, playlistId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Playlist deleted successfully")
                        .build()
        );
    }

    // Xóa bài hát khỏi playlist
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<?> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        Long userId = SecurityUtils.getCurrentUserId();
        playlistService.removeSongFromPlaylist(userId, playlistId, songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Remove Song From Playlist is Successful")
                        .build()
        );
    }


}

