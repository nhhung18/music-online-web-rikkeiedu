package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "otp")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OTP extends BaseObject {
    private String email;
    private String otp;
    private LocalDateTime expireAt;
    private boolean verified;
}
