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
    public Optional<ServerServicesEntity> findByName(String name, String transactionId) {
        return serverServicesRepository.findByName(name);
    }
}
