package com.ra.base_spring_boot.dto.req;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutReq {
    private String accessToken;
}
