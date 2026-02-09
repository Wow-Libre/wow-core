package com.register.wowlibre.domain.port.in.products;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;

import java.util.*;

public interface ProductPort {
    Map<String, List<ProductCategoryModel>> products(String language, String transactionId);

    ProductDto product(String referenceCode, String transactionId);

    ProductEntity getProduct(String referenceCode, String transactionId);

    List<ProductDiscountsDto> productDiscounts(String language, String transactionId);

    void createProduct(CreateProductDto product, String transactionId);

    void updateProduct(Long productId, CreateProductDto product, String transactionId);

    ProductsDetailsDto allProducts(String transactionId);

    void deleteProduct(Long productId, String transactionId);
}
