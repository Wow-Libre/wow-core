package com.register.wowlibre.domain.port.out.packages;

import com.register.wowlibre.infrastructure.entities.transactions.PackagesEntity;

public interface SavePackages {
    void save(PackagesEntity packageEntity, String transactionId);
}
