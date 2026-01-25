package com.register.wowlibre.infrastructure.repositories.products;

import com.register.wowlibre.domain.port.out.products.ObtainProduct;
import com.register.wowlibre.domain.port.out.products.SaveProduct;
import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaProductAdapter implements ObtainProduct, SaveProduct {
    private final ProductRepository productRepository;

    public JpaProductAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductEntity> findByStatusIsTrueAndLanguage(String language, String transactionId) {
        return productRepository.findByRealmIdAndStatusAndLanguage(null, true, language);
    }

    @Override
    public List<ProductEntity> findByStatusIsTrueAndDiscount(String language, String transactionId) {
        return productRepository.findByStatusAndLanguageAndDiscountGreaterThan(true, language, 0);
    }

    @Override
    public Optional<ProductEntity> findByReferenceNumber(String referenceCode, String transactionId) {
        return productRepository.findByReferenceNumber(referenceCode);
    }

    @Override
    public Optional<ProductEntity> findByNameAndLanguage(String name, String language, String transactionId) {
        return productRepository.findByNameAndLanguage(name, language);
    }

    @Override
    public List<ProductEntity> findAllByStatusIsTrue(String transactionId) {
        return productRepository.findByStatus(true);
    }

    @Override
    public Optional<ProductEntity> findById(Long productId, String transactionId) {
        return productRepository.findById(productId);
    }

    @Override
    public void save(ProductEntity product, String transactionId) {
        productRepository.save(product);
    }
}

