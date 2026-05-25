package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.SongResp;
import com.ra.base_spring_boot.services.core.IDownloadService;
import com.ra.base_spring_boot.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/download")
@RequiredArgsConstructor
public class DownloadController {

    private final IDownloadService downloadService;

    @PostMapping("/{songId}")
    public ResponseEntity<?> createDownload( @PathVariable Long songId) {
        Long userId = SecurityUtils.getCurrentUserId();
        downloadService.createDownload(userId, songId);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Created successfully")
                        .build()
        );
    }


    @GetMapping()
    public ResponseEntity<?> getDownloads() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<SongResp> downloads = downloadService.getDownloadsByUser(userId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(downloads)
                        .build()
        );

    }


    @DeleteMapping("/{songId}")
    public ResponseEntity<?> deleteDownload( @PathVariable Long songId) {
        Long userId = SecurityUtils.getCurrentUserId();
        downloadService.deleteDownload(userId, songId);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Delete successfully")
                        .build()
        );
    }
}

