package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.UserStatus;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResp {
    private Long id;
    private String email;
    private String bio;
    private String firstName;
    private String lastName;
    private String profileImage;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

 }


