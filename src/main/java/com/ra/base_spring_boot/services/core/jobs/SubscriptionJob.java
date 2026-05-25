package com.ra.base_spring_boot.services.core.jobs;

import com.ra.base_spring_boot.services.core.ISubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionJob {

    private final ISubscriptionService subscriptionService;


    @Scheduled(cron = "0 0 0 * * ?")
    public void processExpiredSubscriptions() {
        subscriptionService.downgradeExpiredArtists();
    }


}
