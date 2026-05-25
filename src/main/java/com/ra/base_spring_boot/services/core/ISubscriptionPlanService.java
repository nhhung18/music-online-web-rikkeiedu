package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.SubscriptionPlanReq;
import com.ra.base_spring_boot.dto.resp.SubscriptionStatResp;
import com.ra.base_spring_boot.model.SubscriptionPlan;

import java.util.List;

public interface ISubscriptionPlanService {
    List<SubscriptionPlan> getAllPlans();
    SubscriptionPlan createPlan(SubscriptionPlanReq plan);
    void deletePlan(Long id);
    List<SubscriptionStatResp> statBySub();
    List<SubscriptionStatResp> statByStatus();
    List<SubscriptionStatResp> statByMonth();
}
