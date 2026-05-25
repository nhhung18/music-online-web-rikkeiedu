package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.ArtistResp;
import com.ra.base_spring_boot.dto.resp.SongResp;
import com.ra.base_spring_boot.services.core.ISongService;
import com.ra.base_spring_boot.services.core.IUserService;
import com.ra.base_spring_boot.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final ISongService songService;
    private final IUserService userService;

// 4 biến bao gồm: artistId, albumId, form, songFile

    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @PostMapping("/create/{albumId}")
    public ResponseEntity<?> createSongToAlbum(
            @RequestParam Long albumId,
            @RequestParam String title,
            @RequestParam Integer duration,
            @RequestParam("genresId") List<Long> genresId,
            @RequestPart("songFile") final MultipartFile songFile
        ) throws Exception {
            Long artistId = SecurityUtils.getCurrentUserId();
            SongResp song = songService.createSongToAlbum(artistId, albumId, title, duration, genresId, songFile);
            return ResponseEntity.ok().body(
                            ResponseWrapper.builder()
                                            .status(HttpStatus.OK)
                                    .code(200)
                                    .data(song)
                                    .build()
                    );
    }

    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @DeleteMapping("/{songId}/album/{albumId}")
    public ResponseEntity<?> deleteSongFromAlbum(@PathVariable Long albumId, @PathVariable Long songId) {
        songService.removeSongFromAlbum(albumId, songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Delete successfully")
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{songId}/report")
    public ResponseEntity<?> deleteSongReport(@PathVariable Long songId) {
        songService.deleteSongAndNotifyArtist(songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Delete successfully")
                        .build()
        );
    }
    

    @PreAuthorize("hasRole('USER') || hasRole('ADMIN') || hasRole('ARTIST')")
    @PostMapping("/{songId}/play")
    public ResponseEntity<?> playSong(
            @PathVariable Long songId
    ) {
            songService.addToHistory(songId);
        
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Song played successfully")
                        .build()
        );
    }


    @PreAuthorize("hasRole('USER') || hasRole('ADMIN') || hasRole('ARTIST')")
    @GetMapping("/my-history")
    public ResponseEntity<?> getMyRecentlyPlayedSongs(
            @RequestParam(defaultValue = "20") int limit
    ) {
        
        List<SongResp> songs = songService.getRecentlyPlayedSongsByUser(limit);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songs)
                        .build()
        );
    }


    @PreAuthorize("hasRole('USER') || hasRole('ADMIN') || hasRole('ARTIST')")
    @DeleteMapping("/{songId}/history")
    public ResponseEntity<?> removeSongFromHistory(@PathVariable Long songId) {
        songService.removeFromHistory(songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Song removed from history successfully")
                        .build()
        );
    }


    @PreAuthorize("hasRole('USER') || hasRole('ADMIN') || hasRole('ARTIST')")
    @DeleteMapping("/history/clear")
    public ResponseEntity<?> clearHistory() {
        songService.clearHistory();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("History cleared successfully")
                        .build()
        );
    }



    @PreAuthorize("hasRole('USER') || hasRole('ADMIN') || hasRole('ARTIST')")
    @PostMapping("/{songId}/view")
    public ResponseEntity<?> increaseSongView(@PathVariable Long songId) {
        songService.increaseView(songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("View count increased successfully")
                        .build()
        );
    }


    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @PostMapping(value = "upload-song/{songId}")
    public ResponseEntity<?> uploadSong(@PathVariable Long songId, @RequestPart("file") final MultipartFile file) throws Exception {
        Long artistId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songService.uploadSong(artistId, songId, file))
                        .build()
        );
    }

    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @PostMapping(value = "upload-imgage/{songId}")
    public ResponseEntity<?> uploadImage(@PathVariable Long songId, @RequestPart("file") final MultipartFile file) throws Exception {
        Long artistId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songService.uploadSongImage(artistId, songId, file))
                        .build()
        );
    }

    @GetMapping()
    public ResponseEntity<?> getAllSong(Pageable pageable, @RequestParam(required = false) String title) {
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songService.getAllSong(pageable, title))
                        .build()
        );
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<?> getSongsByAlbum(@PathVariable Long albumId) {
        List<SongResp> songs = songService.getSongsByAlbum(albumId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songs)
                        .build()
        );
    }


    @GetMapping("/top-weekly")
    public ResponseEntity<?> getTop15SongsByWeek() {
        List<SongResp> songs = songService.getTop15SongsByWeek();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songs)
                        .build()
        );
    }


    @GetMapping("/new-songs")
    public ResponseEntity<?> getNewSongs() {
        List<SongResp> songs = songService.getNewSong();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songs)
                        .build()
        );
    }


    @GetMapping("/artist/{artistId}")
    public ResponseEntity<?> getSongsByArtist(@PathVariable Long artistId) {
        List<SongResp> songs = songService.getSongsByArtist(artistId);
        if (songs == null || songs.isEmpty()) {
            ArtistResp artist = userService.getArtistById(artistId);
            return ResponseEntity.ok().body(
                    ResponseWrapper.builder()
                            .status(HttpStatus.OK)
                            .code(200)
                            .data(artist)
                            .build()
            );
        }
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songs)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stat/view")
    public ResponseEntity<?> statSongByView(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(201)
                        .data(songService.getTopSongsAllTime(page, size))
                        .build()
        );
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<?> getSongByGenre(@PathVariable Long genreId){
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songService.getSongByGenre(genreId))
                        .build()
        );
    }
}

