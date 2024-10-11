package com.register.wowlibre.infrastructure.repositories.server_services;

import com.register.wowlibre.domain.port.out.server_services.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaServerServicesAdapter implements ObtainServiceServices {
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
}
