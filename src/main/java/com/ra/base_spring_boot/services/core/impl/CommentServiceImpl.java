
package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResp;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Comment;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.ICommentRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.core.ICommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements ICommentService {

    private final ICommentRepository commentRepository;
    private final IUserRepository userRepository;
    private final ISongRepository songRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<CommentResp> getCommentsBySongId(Pageable pageable, Long songId) {
        // Verify song exists
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpBadRequest("Song not found"));

        Page<Comment> comments = commentRepository.findBySongIdAndParentIsNull(pageable, songId);
        return comments.map(comment -> modelMapper.map(comment, CommentResp.class));
    }

    @Override
    public CommentResp addComment(Long userId, CommentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpBadRequest("User not found"));

        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new HttpBadRequest("Song not found"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new HttpBadRequest("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .song(song)
                .parent(parent)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return convertToResponse(savedComment);
    }

    @Override
    public CommentResp updateComment(Long userId, Long commentId, String content) {
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new HttpBadRequest("Comment not found or you don't have permission to update"));

        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);
        return convertToResponse(updatedComment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new HttpBadRequest("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpBadRequest("User not found"));

        boolean canDelete = false;

        if (comment.getUser().getId().equals(userId)) {
            canDelete = true;
        }

        if (comment.getSong().getArtist().getId().equals(userId)) {
            canDelete = true;
        }

        if (!canDelete) {
            throw new HttpBadRequest("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    public CommentResp artistReplyToComment(Long artistId, CommentRequest request) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new HttpBadRequest("Artist not found"));

        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new HttpBadRequest("Song not found"));

        if (!songRepository.existsBySongIdAndArtistId(request.getSongId(), artistId)) {
            throw new HttpBadRequest("You can only reply to comments on your own songs");
        }

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new HttpBadRequest("Parent comment not found"));

            if (!parent.getSong().getArtist().getId().equals(artistId)) {
                throw new HttpBadRequest("You can only reply to comments on your own songs");
            }
        }

        Comment reply = Comment.builder()
                .content(request.getContent())
                .user(artist)
                .song(song)
                .parent(parent)
                .build();

        Comment savedReply = commentRepository.save(reply);
        return convertToResponse(savedReply);
    }

    @Override
    public Page<CommentResp> getCommentsForArtistSong(Pageable pageable, Long artistId, Long songId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new HttpBadRequest("Artist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpBadRequest("Song not found"));

        if (!songRepository.existsBySongIdAndArtistId(songId, artistId)) {
            throw new HttpBadRequest("You don't have permission to view comments for this song");
        }

        Page<Comment> comments = commentRepository.findBySongId(pageable, songId);
        return comments.map(comment -> modelMapper.map(comment, CommentResp.class));
    }


    private CommentResp convertToResponse(Comment comment) {
        List<CommentResp> replies = comment.getReplies() == null
                ? new ArrayList<>()
                : comment.getReplies().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());


        return CommentResp.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName())
                .songId(comment.getSong().getId())
                .songTitle(comment.getSong().getTitle())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(replies)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }


}
