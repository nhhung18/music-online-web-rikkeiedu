package com.ra.base_spring_boot.dto.req;

import lombok.*;

@Getter
@Setter
public class ChangePassword {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
