package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Subscription;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.SubscriptionStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ISubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserAndPlan(User user, SubscriptionPlan plan);
    Optional<Subscription> findFirstByUserIdAndStatusOrderByEndTimeDesc(Long userId, SubscriptionStatus status);

    @Modifying
    @Transactional
    @Query("""
            SELECT s FROM Subscription s
            WHERE s.endTime < :now
            AND s.status = :active
            """)
    List<Subscription> findAllByStatusAndEndTimeBefore(
            @Param("now") LocalDateTime now,
            @Param("active") SubscriptionStatus active);
}
