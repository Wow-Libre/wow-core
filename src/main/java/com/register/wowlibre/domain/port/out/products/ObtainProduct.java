package com.register.wowlibre.domain.port.out.products;

import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;

import java.util.*;

public interface ObtainProduct {
    List<ProductEntity> findByStatusIsTrueAndLanguage(String language, String transactionId);

    List<ProductEntity> findByStatusIsTrueAndDiscount(String language, String transactionId);

    Optional<ProductEntity> findByReferenceNumber(String referenceCode, String transactionId);

    Optional<ProductEntity> findByNameAndLanguage(String name, String language, String transactionId);

    List<ProductEntity> findAllByStatusIsTrue(String transactionId);

    Optional<ProductEntity> findById(Long productId, String transactionId);
}
