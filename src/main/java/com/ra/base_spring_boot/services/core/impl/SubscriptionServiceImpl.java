package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.resp.SubscriptionResponse;
import com.ra.base_spring_boot.exception.HttpForbidden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.SubscriptionStatus;
import com.ra.base_spring_boot.repository.ISubscriptionPlanRepository;
import com.ra.base_spring_boot.repository.ISubscriptionRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.core.IRoleService;
import com.ra.base_spring_boot.services.core.ISubscriptionService;
import com.ra.base_spring_boot.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements ISubscriptionService {
    private final ISubscriptionRepository subscriptionRepository;
    private final ISubscriptionPlanRepository subscriptionPlanRepository;
    private final IUserRepository userRepository;
    private final IRoleService roleService;


    @Override
    public SubscriptionResponse getMyActiveSubscription() {
        Long userId = SecurityUtils.getCurrentUserId();
        Subscription sub = subscriptionRepository
                .findFirstByUserIdAndStatusOrderByEndTimeDesc(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);
        if (sub == null) return null;
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .plan(sub.getPlan())
                .startTime(sub.getStartTime())
                .endTime(sub.getEndTime())
                .status(sub.getStatus())
                .build();
    }

    @Override
    public void cancelSubscription(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Subscription not found"));
        if (!currentUserId.equals(subscription.getUser().getId())) {
            throw new HttpForbidden("You don't have permission");
        }

        // Downgrade role nếu là Artist Plan
        if (subscription.getPlan().getPlanName().equals("Artist Plan")) {
            roleService.downgradeToUser(subscription.getUser().getId());
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }


    @Override
    public void addOrExtend(Long planId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));
        SubscriptionPlan subscriptionPlan=subscriptionPlanRepository.findById(planId)
                .orElseThrow(()-> new HttpNotFound("Plan not found"));
        Subscription subscription = subscriptionRepository.findByUserAndPlan(user, subscriptionPlan)
                .map(existing -> updateSubscription(existing, subscriptionPlan))
                .orElseGet(()-> createNewSubscription(user, subscriptionPlan));

        subscriptionRepository.save(subscription);

        if(subscriptionPlan.getPlanName().equals("Artist Plan")){
            roleService.upgradeToArtist(userId);
        }
    }

    private Subscription updateSubscription(Subscription subscription, SubscriptionPlan subscriptionPlan){
        LocalDateTime now = LocalDateTime.now();
        if (subscription.getStatus().isInactive()) {
            subscription.setStartTime(now);
            subscription.setEndTime(now.plusDays(subscriptionPlan.getDurationDay()));
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        } else {
            subscription.setEndTime(subscription.getEndTime().plusDays(subscriptionPlan.getDurationDay()));
        }
        return subscription;
    }

    private Subscription createNewSubscription(User user, SubscriptionPlan subscriptionPlan) {
        LocalDateTime now = LocalDateTime.now();
        return Subscription.builder()
                .user(user)
                .plan(subscriptionPlan)
                .startTime(now)
                .endTime(now.plusDays(subscriptionPlan.getDurationDay()))
                .status(SubscriptionStatus.ACTIVE)
                .build();
    }

    @Override
    @Transactional
    public List<Subscription> expireSubscriptions(){
        List<Subscription> expired= subscriptionRepository.findAllByStatusAndEndTimeBefore(
                LocalDateTime.now(),
                SubscriptionStatus.ACTIVE
        );

        for(Subscription s: expired){
            s.setStatus(SubscriptionStatus.EXPIRED);
        }

        return subscriptionRepository.saveAll(expired);
    }

    @Override
    @Transactional
    public void downgradeExpiredArtists() {
        List<Subscription> expiredSubs = expireSubscriptions();

        List<User> expiredArtists = expiredSubs.stream()
                .filter(s -> s.getPlan().getPlanName().equals("Artist Plan"))
                .map(Subscription::getUser)
                .toList();
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByRoleName(RoleName.ROLE_USER));
        for (User u : expiredArtists) {
            u.setRoles(roles);
        }

        userRepository.saveAll(expiredArtists);
    }

    @Override
    public List<String> getBenefits() {
        Long userId = SecurityUtils.getCurrentUserId();

        Subscription sub = subscriptionRepository
                .findFirstByUserIdAndStatusOrderByEndTimeDesc(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (sub != null && sub.getPlan() != null && sub.getPlan().getBenefits() != null) {
            return sub.getPlan().getBenefits().stream()
                    .map(benefit -> benefit.getCode().name())
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
