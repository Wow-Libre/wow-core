package com.register.wowlibre.domain.port.out.packages;

import com.register.wowlibre.infrastructure.entities.transactions.PackagesEntity;
import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;

import java.util.List;

public interface ObtainPackages {
    List<PackagesEntity> findByProductId(ProductEntity product, String transactionId);
}
