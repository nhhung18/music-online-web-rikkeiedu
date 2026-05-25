package com.ra.base_spring_boot.dto.req;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class SubscriptionPlanReq {
    private String planName;
    private Double price;
    private Integer durationDay;
    private String description;
    private List<String> benefits;
}
