package com.register.wowlibre.domain.port.out.product_category;

import com.register.wowlibre.infrastructure.entities.transactions.ProductCategoryEntity;

public interface SaveProductCategory {
    ProductCategoryEntity save(ProductCategoryEntity productCategoryEntity);
}
