package com.ra.base_spring_boot.dto.req;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlaylistReq {
    private String name;
    private Boolean isPublic = false;
}
