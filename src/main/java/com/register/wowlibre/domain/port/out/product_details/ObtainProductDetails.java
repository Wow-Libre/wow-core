package com.register.wowlibre.domain.port.out.product_details;

import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface ObtainProductDetails {
    List<ProductDetailsEntity> findByProductId(ProductEntity product);
}
