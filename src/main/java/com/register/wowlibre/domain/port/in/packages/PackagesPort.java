package com.register.wowlibre.domain.port.in.packages;

import com.register.wowlibre.infrastructure.entities.transactions.PackagesEntity;

public interface PackagesPort {
    void save(PackagesEntity packagesEntity, String transactionId);
}
