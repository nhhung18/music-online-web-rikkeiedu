package com.ra.base_spring_boot.dto.resp;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GenreResp {
    private Long id;
    private String genreName;
    private String genreUrl;
    private Long views;
}
