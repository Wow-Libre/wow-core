package com.register.wowlibre.application.services.server_services;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.server_services.*;
import com.register.wowlibre.domain.port.out.server_services.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class ServerServicesService implements ServerServicesPort {
    private final ObtainServiceServices obtainServiceServices;

    public ServerServicesService(ObtainServiceServices obtainServiceServices) {
        this.obtainServiceServices = obtainServiceServices;
    }

    @Override
    public List<ServerServicesModel> findByServerId(Long serverId, String transactionId) {
        return obtainServiceServices.findByServerId(serverId, transactionId).stream().map(this::mapToModel).toList();
    }

    @Override
    public ServerServicesModel findByNameAndServerId(String name, Long serverId, String transactionId) {
        return obtainServiceServices.findByNameAndServerId(name, serverId, transactionId).map(this::mapToModel).orElse(null);
    }

    @Override
    public List<ServerServicesModel> findByServersAvailableLoa(String transactionId) {
        return obtainServiceServices.findByServersAvailableRequestLoa(transactionId).stream().map(this::mapToModel).toList();
    }

    private ServerServicesModel mapToModel(ServerServicesEntity serverServicesEntity) {
        return new ServerServicesModel(serverServicesEntity.getId(), serverServicesEntity.getName(),
                serverServicesEntity.getAmount(), serverServicesEntity.getServerId().getId(),
                serverServicesEntity.getServerId().getName());
    }
}
