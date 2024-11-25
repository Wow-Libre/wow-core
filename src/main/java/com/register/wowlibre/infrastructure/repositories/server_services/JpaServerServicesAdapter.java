package com.register.wowlibre.infrastructure.repositories.server_services;

import com.register.wowlibre.domain.port.out.server_services.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaServerServicesAdapter implements ObtainServiceServices, SaveServiceServices {
    private final ServerServicesRepository serverServicesRepository;

    public JpaServerServicesAdapter(ServerServicesRepository serverServicesRepository) {
        this.serverServicesRepository = serverServicesRepository;
    }


    @Override
    public List<ServerServicesEntity> findByServerId(Long serverId, String transactionId) {
        return serverServicesRepository.findByServerId_Id(serverId);
    }

    @Override
    public Optional<ServerServicesEntity> findByNameAndServerId(String name, Long serverId, String transactionId) {
        return serverServicesRepository.findByNameAndServerId_id(name, serverId);
    }

    @Override
    public List<ServerServicesEntity> findByServersAvailableRequestLoa(String transactionId) {
        return serverServicesRepository.findActiveServerServicesWithAmountGreaterThanZero();
    }

    @Override
    public Optional<ServerServicesEntity> findById(Long id) {
        return serverServicesRepository.findById(id);
    }

    @Override
    public void save(ServerServicesEntity serverServicesEntity, String transactionId) {
        serverServicesRepository.save(serverServicesEntity);
    }
}
