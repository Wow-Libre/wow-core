package com.register.wowlibre.application.services.product_details;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.product_details.*;
import com.register.wowlibre.domain.port.out.product_details.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class ProductDetailsService implements ProductDetailsPort {
    private final ObtainProductDetails obtainProductDetails;

    public ProductDetailsService(ObtainProductDetails obtainProductDetails) {
        this.obtainProductDetails = obtainProductDetails;
    }

    @Override
    public List<ProductDetailModel> findByProductId(ProductEntity product, String transactionId) {
        return obtainProductDetails.findByProductId(product).stream().map(this::mapToModel).toList();
    }

    private ProductDetailModel mapToModel(ProductDetailsEntity productDetails) {
        return new ProductDetailModel(productDetails.getId(), productDetails.getProductId().getId(),
                productDetails.getTitle(), productDetails.getDescription(), productDetails.getImgUrl());
    }
}
