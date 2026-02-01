package com.register.wowlibre.infrastructure.repositories.packages;

import com.register.wowlibre.infrastructure.entities.transactions.PackagesEntity;
import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PackagesRepository extends CrudRepository<PackagesEntity, Long> {
    List<PackagesEntity> findByProductId_Id(Long productId);
    List<PackagesEntity> findByProductId(ProductEntity product);
}
