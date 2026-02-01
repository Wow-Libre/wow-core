package com.register.wowlibre.infrastructure.repositories.product_category;

import com.register.wowlibre.domain.port.out.product_category.ObtainProductCategory;
import com.register.wowlibre.domain.port.out.product_category.SaveProductCategory;
import com.register.wowlibre.infrastructure.entities.transactions.ProductCategoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaProductCategoryAdapter implements ObtainProductCategory, SaveProductCategory {
    private final ProductCategoryRepository productCategoryRepository;

    public JpaProductCategoryAdapter(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public Optional<ProductCategoryEntity> findByName(String name, String transactionId) {
        return productCategoryRepository.findByName(name);
    }

    @Override
    public List<ProductCategoryEntity> findAll(String transactionId) {
        return (List<ProductCategoryEntity>) productCategoryRepository.findAll();
    }

    @Override
    public Optional<ProductCategoryEntity> findById(Long id, String transactionId) {
        return productCategoryRepository.findById(id);
    }

    @Override
    public ProductCategoryEntity save(ProductCategoryEntity productCategoryEntity) {
        return productCategoryRepository.save(productCategoryEntity);
    }
}
