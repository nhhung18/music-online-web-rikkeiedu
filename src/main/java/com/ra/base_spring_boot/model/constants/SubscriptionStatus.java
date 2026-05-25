package com.ra.base_spring_boot.model.constants;

public enum SubscriptionStatus {
    ACTIVE,
    EXPIRED,
    CANCELLED;

    public boolean isInactive(){
        return this == EXPIRED || this == CANCELLED;
    }
}
