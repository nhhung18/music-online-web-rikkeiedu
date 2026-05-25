package com.ra.base_spring_boot.dto.req;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OtpReq {
    private String email;
    private String otp;
}
