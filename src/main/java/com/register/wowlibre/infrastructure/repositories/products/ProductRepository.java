package com.register.wowlibre.infrastructure.repositories.products;

import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByReferenceNumber(String referenceNumber);
    List<ProductEntity> findByRealmIdAndStatus(Long realmId, Boolean status);
    List<ProductEntity> findByRealmIdAndStatusAndLanguage(Long realmId, Boolean status, String language);
    List<ProductEntity> findByNameAndRealmIdAndLanguage(String name, Long realmId, String language);
    List<ProductEntity> findByStatusAndLanguageAndDiscountGreaterThan(Boolean status, String language, Integer discount);
    Optional<ProductEntity> findByNameAndLanguage(String name, String language);
    List<ProductEntity> findByStatus(Boolean status);
}
