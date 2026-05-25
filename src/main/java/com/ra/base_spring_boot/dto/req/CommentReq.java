package com.ra.base_spring_boot.dto.req;

import com.ra.base_spring_boot.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReq {
    Long userId;
    Long songId;
    String content;
}
