package com.ra.base_spring_boot.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistDto {
    private Long id;
    private String bio;
    private String email;
    private String firstName;
    private String lastName;
    private String profileImage;
    private String userStatus;
    private LocalDateTime lastUpdated;
}
