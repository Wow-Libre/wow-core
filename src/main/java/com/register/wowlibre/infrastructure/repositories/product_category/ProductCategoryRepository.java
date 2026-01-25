package com.register.wowlibre.infrastructure.repositories.product_category;

import com.register.wowlibre.infrastructure.entities.transactions.ProductCategoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends CrudRepository<ProductCategoryEntity, Long> {
    Optional<ProductCategoryEntity> findByName(String name);
    List<ProductCategoryEntity> findAll();
}
