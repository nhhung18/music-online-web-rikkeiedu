package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.resp.SubscriptionResponse;
import com.ra.base_spring_boot.model.Subscription;

import java.util.List;

public interface ISubscriptionService {
    SubscriptionResponse getMyActiveSubscription();
    void cancelSubscription(Long id);
    void addOrExtend(Long planId, Long userId);
    List<Subscription> expireSubscriptions();
    void downgradeExpiredArtists();
    List<String> getBenefits();
}
