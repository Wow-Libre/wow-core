package com.register.wowlibre.infrastructure.repositories.packages;

import com.register.wowlibre.domain.port.out.packages.ObtainPackages;
import com.register.wowlibre.domain.port.out.packages.SavePackages;
import com.register.wowlibre.infrastructure.entities.transactions.PackagesEntity;
import com.register.wowlibre.infrastructure.entities.transactions.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaPackagesAdapter implements ObtainPackages, SavePackages {
    private final PackagesRepository packagesRepository;

    public JpaPackagesAdapter(PackagesRepository packagesRepository) {
        this.packagesRepository = packagesRepository;
    }

    @Override
    public List<PackagesEntity> findByProductId(ProductEntity product, String transactionId) {
        return packagesRepository.findByProductId(product);
    }

    @Override
    public void save(PackagesEntity packageEntity, String transactionId) {
        packagesRepository.save(packageEntity);
    }
}
