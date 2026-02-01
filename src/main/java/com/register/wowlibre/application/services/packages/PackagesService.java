package com.register.wowlibre.application.services.packages;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.packages.*;
import com.register.wowlibre.domain.port.out.packages.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class PackagesService implements PackagesPort {
    private final ObtainPackages obtainPackages;
    private final SavePackages savePackages;

    public PackagesService(ObtainPackages obtainPackages, SavePackages savePackages) {
        this.obtainPackages = obtainPackages;
        this.savePackages = savePackages;
    }


    @Override
    public void save(PackagesEntity packagesEntity, String transactionId) {
        savePackages.save(packagesEntity, transactionId);
    }

    @Override
    public List<ItemQuantityModel> findByProductId(ProductEntity product, String transactionId) {
        return obtainPackages.findByProductId(product, transactionId).stream()
                .map(packages -> new ItemQuantityModel(packages.getCodeCore(), 1)).toList();
    }
}
