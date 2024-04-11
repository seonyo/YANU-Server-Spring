package com.bbogle.yanu.repository;


import com.bbogle.yanu.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByTitleContaining(String keyword);
    List<ProductEntity> findAllByTitleContainingAndCategory(String keyword, String type);

}