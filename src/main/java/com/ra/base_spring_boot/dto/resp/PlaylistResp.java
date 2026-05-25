package com.ra.base_spring_boot.dto.resp;

import lombok.Data;

import java.util.List;

@Data
public class PlaylistResp {
    private Long id;
    private String name;
    private Boolean isPublic;
    private Long userId;
    private String username;
    List<SongResp> songs;
}
