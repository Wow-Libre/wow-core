package com.register.wowlibre.infrastructure.repositories.server_services;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.port.out.realm_services.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaServerServicesAdapter implements ObtainRealmServices, SaveRealmServices {
    private final RealmServicesRepository realmServicesRepository;

    public JpaServerServicesAdapter(RealmServicesRepository realmServicesRepository) {
        this.realmServicesRepository = realmServicesRepository;
    }

    @Override
    public List<RealmServicesEntity> findByRealmId(Long realmId, String transactionId) {
        return realmServicesRepository.findByRealmId_Id(realmId);
    }

    @Override
    public Optional<RealmServicesEntity> findByNameAndRealmId(RealmServices name, Long realmId, String transactionId) {
        return realmServicesRepository.findByNameAndRealmId_Id(name, realmId);
    }

    @Override
    public List<RealmServicesEntity> findByServersAvailableRequestLoa(String transactionId) {
        return realmServicesRepository.findActiveRealmServicesWithAmountGreaterThanZero(RealmServices.BANK);
    }

    @Override
    public Optional<RealmServicesEntity> findById(Long id) {
        return realmServicesRepository.findById(id);
    }

    @Override
    public void save(RealmServicesEntity realmServicesEntity, String transactionId) {
        realmServicesRepository.save(realmServicesEntity);
    }
}
