package com.register.wowlibre.domain.port.in;


import com.register.wowlibre.domain.model.ProductDetailModel;
import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;

import java.util.List;

public interface ProductDetailsPort {
    List<ProductDetailModel> findByProductId(ProductEntity product, String transactionId);
}
