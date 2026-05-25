package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.constants.SubscriptionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults( level = AccessLevel.PRIVATE)
public class SubscriptionResponse {
    Long id;
    SubscriptionPlan plan;
    LocalDateTime startTime;
    LocalDateTime endTime;
    SubscriptionStatus status;
}
