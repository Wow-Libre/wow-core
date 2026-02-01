package com.register.wowlibre.domain.port.out.product_details;

import com.register.wowlibre.infrastructure.entities.transactions.ProductDetailsEntity;

public interface SaveProductDetails {
    ProductDetailsEntity save(ProductDetailsEntity productDetailsEntity);
    void delete(ProductDetailsEntity productDetailsEntity);
}
