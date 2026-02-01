package com.register.wowlibre.infrastructure.repositories.product_details;

import com.register.wowlibre.domain.port.out.product_details.ObtainProductDetails;
import com.register.wowlibre.domain.port.out.product_details.SaveProductDetails;
import com.register.wowlibre.infrastructure.entities.transactions.ProductDetailsEntity;
import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaProductDetailsAdapter implements ObtainProductDetails, SaveProductDetails {
    private final ProductDetailsRepository productDetailsRepository;

    public JpaProductDetailsAdapter(ProductDetailsRepository productDetailsRepository) {
        this.productDetailsRepository = productDetailsRepository;
    }

    @Override
    public List<ProductDetailsEntity> findByProductId(ProductEntity product) {
        return productDetailsRepository.findByProductId_Id(product.getId());
    }

    @Override
    public ProductDetailsEntity save(ProductDetailsEntity productDetailsEntity) {
        return productDetailsRepository.save(productDetailsEntity);
    }

    @Override
    public void delete(ProductDetailsEntity productDetailsEntity) {
        productDetailsRepository.delete(productDetailsEntity);
    }
}
