package com.ra.base_spring_boot.controller;



import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.ChangePassword;
import com.ra.base_spring_boot.dto.req.UpdatePassword;
import com.ra.base_spring_boot.dto.req.UserUpdate;
import com.ra.base_spring_boot.dto.resp.ArtistResp;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.services.core.IUserService;
import com.ra.base_spring_boot.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        Long id = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(userService.getUserById(id))
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllUser(Pageable pageable, @RequestParam(required = false) String name) {
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(userService.getAllUser(pageable, name))
                        .build()
        );
    }


    @GetMapping("/artist")
    public ResponseEntity<?> getAllArtists(
            Pageable pageable,
            @RequestParam(required = false) String name
    ) {
        Page<ArtistResp> artists = userService.getAllArtists(pageable, name);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(artists)
                        .build()
        );
    }


    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') || hasRole('ARTIST') || hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdate userUpdate) {
        User updatedUser = userService.updateUser(userUpdate);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(updatedUser)
                        .build()
        );
    }

    @PreAuthorize("hasRole('USER') || hasRole('ARTIST') || hasRole('ADMIN')")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassword changePassword) {

        userService.changePassword(changePassword);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Password changed successfully")
                        .build()
        );
    }

    @PutMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        userService.forgotPassword(email);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("OTP has been sent to your email")
                        .build()
        );
    }



    @PutMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody UpdatePassword request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Password reset successfully")
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("status/{userId}")
    public ResponseEntity<?> changeUserStatus(
            @PathVariable Long userId,
            @RequestParam(value = "value") String userStatus) {

        userService.changeUserStatus(userId, userStatus);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Change successfully")
                        .build()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("upload-image")
    public ResponseEntity<?> uploadImage (@RequestPart("file") MultipartFile file) throws Exception {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.uploadImage(userId, file);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Upload Successfully")
                        .build()
        );
    }

    //  User stats
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("status-stats")
    public ResponseEntity<?> getUserStats (Pageable pageable, @RequestParam(value = "status") String status) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("users", userService.getUserStats(pageable, status));
        responseData.put("total", userService.countUserStatus(status));
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(responseData)
                        .build()
        );
    }


    @GetMapping("/featured/artists")
    public ResponseEntity<?> getFeaturedArtists() {
        List<ArtistResp> artists = userService.getFeaturedArtists();
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(artists)
                        .build()
        );
    }
}

