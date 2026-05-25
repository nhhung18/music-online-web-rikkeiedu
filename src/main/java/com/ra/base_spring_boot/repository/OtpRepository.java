package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OTP, Integer> {
    Optional<OTP> findByEmail(String email);
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
}
