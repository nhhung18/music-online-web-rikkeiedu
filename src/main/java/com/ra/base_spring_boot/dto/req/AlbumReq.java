package com.ra.base_spring_boot.dto.req;

import com.ra.base_spring_boot.model.constants.AlbumType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class AlbumReq {
    @NotBlank(message = "Tiêu đề album không được để trống")
    private String title;

    @NotNull(message = "Ngày phát hành không được để trống")
    private LocalDateTime releaseDate;

    @NotBlank(message = "Ảnh bìa album không được để trống")
    private String coverImage;

    @NotNull(message = "Loại album không được để trống")
    private AlbumType type;
}

