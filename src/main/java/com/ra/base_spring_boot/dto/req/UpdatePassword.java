package com.ra.base_spring_boot.dto.req;

import lombok.*;

@Getter
@Setter
public class UpdatePassword {
    private String email;
    private String newPassword;
    private String confirmPassword;
}
