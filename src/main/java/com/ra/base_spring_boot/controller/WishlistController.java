package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.SongIdReq;
import com.ra.base_spring_boot.dto.resp.SongResp;
import com.ra.base_spring_boot.services.core.IWishlistService;
import com.ra.base_spring_boot.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final IWishlistService wishlistService;

    @GetMapping()
    public ResponseEntity<?> getWishlist() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(wishlistService.getWishlist(userId))
                        .build()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToWishlist(
            @RequestBody SongIdReq song
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        SongResp songs = wishlistService.addToWishlist(userId, song.getId() );
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(songs)
                        .build()
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromWishlist(
            @RequestBody SongIdReq song
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        wishlistService.removeFromWishlist(userId, song.getId());
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Removed from wishlist successfully!")
                        .build()
        );
    }
}
