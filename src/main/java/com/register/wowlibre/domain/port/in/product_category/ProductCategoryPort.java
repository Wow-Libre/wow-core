package com.register.wowlibre.domain.port.in.product_category;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.infrastructure.entities.transactions.ProductCategoryEntity;

import java.util.*;

public interface ProductCategoryPort {
    List<ProductCategoryDto> findAllProductCategories(String transactionId);

    void createProductCategory(String name, String description, String disclaimer, String transactionId);

    ProductCategoryEntity findById(Long Id, String transactionId);

}
