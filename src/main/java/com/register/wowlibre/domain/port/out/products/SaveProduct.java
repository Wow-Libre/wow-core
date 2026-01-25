package com.register.wowlibre.domain.port.out.products;

import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;

public interface SaveProduct {
    void save(ProductEntity product, String transactionId);
}
