package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.User;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SongResp {
    private Long id;
    private String title;
    private Integer duration;
    private Album album;
    private User artist;
    private String songUrl;
    private String songImgUrl;
    private Long views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Genre> genres;
}
