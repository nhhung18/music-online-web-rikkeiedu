package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUserResp {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImage;
    private String bio;
    private UserStatus status;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

}
