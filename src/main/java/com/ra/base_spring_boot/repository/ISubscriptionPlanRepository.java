package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.constants.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ISubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByPlanName(PlanType plan);


    @Query("""
            SELECT sp.planName, COUNT(s)
            FROM Subscription s JOIN s.plan sp
            GROUP BY sp.planName
            ORDER BY COUNT(s) DESC
            """)
    List<Object[]> getSubPlanBySub();

    @Query("""
            SELECT CAST(s.status AS string), COUNT(s)
            FROM Subscription s JOIN s.plan sp
            WHERE sp.planName != 'FREE'
            GROUP BY s.status
            ORDER BY COUNT(s) DESC
            """)
    List<Object[]> getSubPlanByStatus();

    @Query("""
            SELECT sp.planName, COUNT(s)
            FROM Subscription s JOIN s.plan sp
            WHERE s.status = 'ACTIVE' AND s.updatedAt >= :monthStart
            GROUP BY sp.planName
            ORDER BY COUNT(s) DESC
            """)
    List<Object[]> getSubPlanByMonth(@Param("monthStart") LocalDateTime monthStart);
}

