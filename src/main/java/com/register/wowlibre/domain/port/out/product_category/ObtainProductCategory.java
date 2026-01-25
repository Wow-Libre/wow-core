package com.register.wowlibre.domain.port.out.product_category;

import com.register.wowlibre.infrastructure.entities.transactions.ProductCategoryEntity;

import java.util.List;
import java.util.Optional;

public interface ObtainProductCategory {
    Optional<ProductCategoryEntity> findByName(String name, String transactionId);
    List<ProductCategoryEntity> findAll(String transactionId);
    Optional<ProductCategoryEntity> findById(Long id, String transactionId);
}
