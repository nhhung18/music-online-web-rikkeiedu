package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.SubscriptionPlanReq;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.services.core.ISubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final ISubscriptionPlanService subscriptionPlanService;

    // Hiển thị các gói
    @GetMapping
    public ResponseEntity<?> getAllPlans() {
        List<SubscriptionPlan> subscriptionPlans = subscriptionPlanService.getAllPlans();
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(subscriptionPlans)
                        .build()
        );
    }


    @PostMapping
    public ResponseEntity<?> createPlan(@Valid @RequestBody SubscriptionPlanReq plan) {

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(subscriptionPlanService.createPlan(plan))
                        .build()
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable Long id) {
        subscriptionPlanService.deletePlan(id);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Subscription plan deleted successfully.")
                        .build()
        );
    }


    @GetMapping("/stat/status")
    public ResponseEntity<?> statSubPlanByStatus() {
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(201)
                        .data(subscriptionPlanService.statByStatus())
                        .build()
        );
    }

    @GetMapping("/stat/month")
    public ResponseEntity<?> statSubPlanByMonth() {
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(201)
                        .data(subscriptionPlanService.statByMonth())
                        .build()
        );
    }
}

