package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInvalidatedTokenRepository extends JpaRepository<InvalidatedToken,String> {
}
