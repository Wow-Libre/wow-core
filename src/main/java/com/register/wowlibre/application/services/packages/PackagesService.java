package com.register.wowlibre.application.services.packages;

import com.register.wowlibre.domain.port.in.packages.PackagesPort;
import com.register.wowlibre.domain.port.out.packages.ObtainPackages;
import com.register.wowlibre.domain.port.out.packages.SavePackages;
import com.register.wowlibre.infrastructure.entities.transactions.PackagesEntity;
import org.springframework.stereotype.Service;

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
}
