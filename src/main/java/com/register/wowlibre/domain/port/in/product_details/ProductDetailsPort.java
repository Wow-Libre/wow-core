package com.register.wowlibre.domain.port.in.product_details;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;

import java.util.*;

public interface ProductDetailsPort {
    List<ProductDetailModel> findByProductId(ProductEntity product, String transactionId);
}
