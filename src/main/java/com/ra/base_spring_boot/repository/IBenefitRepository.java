package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Benefit;
import com.ra.base_spring_boot.model.constants.BenefitCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface IBenefitRepository extends JpaRepository<Benefit, Long> {
    Set<Benefit> findAllByCodeIn(List<BenefitCode> benefits);
}
