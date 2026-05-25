
package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICommentService {
    Page<CommentResp> getCommentsBySongId(Pageable pageable, Long songId);
    CommentResp addComment(Long userId, CommentRequest request);
    CommentResp updateComment(Long userId, Long commentId, String content);
    void deleteComment(Long userId, Long commentId);
    CommentResp artistReplyToComment(Long artistId, CommentRequest request);
    Page<CommentResp> getCommentsForArtistSong(Pageable pageable, Long artistId, Long songId);

}
