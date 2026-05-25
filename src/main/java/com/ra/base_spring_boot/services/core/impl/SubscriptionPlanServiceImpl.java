package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.SubscriptionPlanReq;
import com.ra.base_spring_boot.dto.resp.SubscriptionStatResp;
import com.ra.base_spring_boot.model.Benefit;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.constants.BenefitCode;
import com.ra.base_spring_boot.repository.IBenefitRepository;
import com.ra.base_spring_boot.repository.ISubscriptionPlanRepository;
import com.ra.base_spring_boot.services.core.ISubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements ISubscriptionPlanService {

    private final ISubscriptionPlanRepository subscriptionPlanRepository;
    private final IBenefitRepository benefitRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanRepository.findAll();
    }

    @Override
    public SubscriptionPlan createPlan(SubscriptionPlanReq planReq) {
        Set<Benefit> benefits= benefitRepository.findAllByCodeIn(planReq.getBenefits()
                .stream().map(BenefitCode::valueOf).toList());
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .planName(planReq.getPlanName())
                .price(planReq.getPrice())
                .durationDay(planReq.getDurationDay())
                .description(planReq.getDescription())
                .benefits(benefits)
                .build();

        return subscriptionPlanRepository.save(plan);
    }

    @Override
    public void deletePlan(Long id) {
        if (!subscriptionPlanRepository.existsById(id)) {
            throw new RuntimeException("Subscription Plan not found with ID: " + id);
        }
        subscriptionPlanRepository.deleteById(id);
    }

    @Override
    public List<SubscriptionStatResp> statBySub() {
        List<Object[]> results= subscriptionPlanRepository.getSubPlanBySub();

        return results.stream().map(row->
                        SubscriptionStatResp.builder()
                                .name((String) row[0])
                                .value(((Number) row[1]).longValue())
                                .build())
                .toList();
    }

    @Override
    public List<SubscriptionStatResp> statByStatus() {
        List<Object[]> results= subscriptionPlanRepository.getSubPlanByStatus();

        return results.stream().map(row->
                        SubscriptionStatResp.builder()
                                .name((String) row[0])
                                .value(((Number) row[1]).longValue())
                                .build())
                .toList();
    }

    @Override
    public List<SubscriptionStatResp> statByMonth() {
        LocalDateTime monthStart = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();
        List<Object[]> results= subscriptionPlanRepository.getSubPlanByMonth(monthStart);

        return results.stream().map(row->
                        SubscriptionStatResp.builder()
                                .name((String) row[0])
                                .value(((Number) row[1]).longValue())
                                .build())
                .toList();
    }
}
