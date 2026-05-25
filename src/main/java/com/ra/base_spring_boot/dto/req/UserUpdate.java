package com.ra.base_spring_boot.dto.req;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserUpdate {
    private String firstName;
    private String lastName;
    private String profileImage;
    private String bio;
    private LocalDateTime updateAt = LocalDateTime.now();

}
