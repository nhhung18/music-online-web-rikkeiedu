package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.constants.PlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanUserResp {
    private User id;
    private PlanType planType;
}
