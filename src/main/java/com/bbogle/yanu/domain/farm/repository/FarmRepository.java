package com.bbogle.yanu.domain.farm.repository;

import com.bbogle.yanu.domain.farm.domain.FarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmRepository extends JpaRepository<FarmEntity, Long> {
    FarmEntity findByUserId(Long user_id);
    boolean existsByUserId(Long user_id);
    List<FarmEntity> findAllByBusinessNameContaining(String keyword);
}
