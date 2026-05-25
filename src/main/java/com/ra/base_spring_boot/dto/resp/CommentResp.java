
package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResp {
    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private Long songId;
    private String songTitle;
    private Long parentId;
    private List<CommentResp> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
