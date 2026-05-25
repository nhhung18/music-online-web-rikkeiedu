package com.ra.base_spring_boot.controller;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.services.core.IUserFavorSongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/favor-song")
@CrossOrigin("*")
public class UserFavorSongController {
    @Autowired
    private IUserFavorSongService iuserFavorSongService;

    @PostMapping("/add/{userId}/{songId}")
    public ResponseEntity<?> addFavorSong(@PathVariable Long userId, @PathVariable Long songId) throws Exception {
        iuserFavorSongService.addFavorSong(userId, songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Add favorite song to user is Successfully!!!")
                        .build()
        );
    }

    @GetMapping()
    public ResponseEntity<?> getFavorSong() {
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(iuserFavorSongService.getFavorSong())
                        .build()
        );
    }

    @DeleteMapping("/delete/{userId}/{songId}")
    public ResponseEntity<?> deleteFavorById(@PathVariable Long userId, @PathVariable Long songId) throws Exception {
        iuserFavorSongService.deleteFavorById(userId, songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Delete favorite song is Successfully!!!")
                        .build()
        );
    }
}
