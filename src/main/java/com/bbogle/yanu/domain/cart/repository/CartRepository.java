package com.bbogle.yanu.domain.cart.repository;

import com.bbogle.yanu.domain.cart.domain.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);

    List<CartEntity> findAllByUserId(Long userId);
}