
package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResp;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.services.core.ICommentService;
import com.ra.base_spring_boot.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @PreAuthorize("hasRole('ARTIST') || hasRole('ADMIN')")
    @GetMapping("/song/{songId}/comment")
    public ResponseEntity<?> getCommentsBySong(Pageable pageable, @PathVariable Long songId) {
        Page<CommentResp> comments = commentService.getCommentsBySongId(pageable, songId);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(comments)
                        .build()
        );
    }

    // CommentRequest: content, songId, parentId(comment parent)
    @PostMapping("/add")
    public ResponseEntity<?> addComment(
            @Valid @RequestBody CommentRequest request
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        CommentResp comment = commentService.addComment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data(comment)
                        .build()
        );
    }

    @PutMapping("/{commentId}/update")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @PathVariable String request
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        CommentResp comment = commentService.updateComment(userId, commentId, request);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(comment)
                        .build()
        );
    }

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Comment deleted successfully")
                        .build()
        );
    }

    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping("/reply")
    public ResponseEntity<?> artistReplyToComment(
            @Valid @RequestBody CommentRequest request
    ) {
        Long artistId = SecurityUtils.getCurrentUserId();
        CommentResp reply = commentService.artistReplyToComment(artistId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data(reply)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping("/artist/{songId}")
    public ResponseEntity<?> getCommentsForArtistSong(
            Pageable pageable,
            @PathVariable Long songId
    ) {
        Long artistId = SecurityUtils.getCurrentUserId();
        Page<CommentResp> comments = commentService.getCommentsForArtistSong(pageable, artistId, songId);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(comments)
                        .build()
        );
    }
}
