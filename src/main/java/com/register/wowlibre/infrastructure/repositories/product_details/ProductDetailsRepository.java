package com.register.wowlibre.infrastructure.repositories.product_details;

import com.register.wowlibre.infrastructure.entities.transactions.ProductDetailsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductDetailsRepository extends CrudRepository<ProductDetailsEntity, Long> {
    List<ProductDetailsEntity> findByProductId_Id(Long productId);
}
