package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.AlbumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumResp {
    private Long id;
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime releaseDate;
    private String title;
    private Long artistId;
    private String artistName;
    private Long views;

}
