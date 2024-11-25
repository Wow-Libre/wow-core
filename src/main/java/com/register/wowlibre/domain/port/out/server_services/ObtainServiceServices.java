package com.register.wowlibre.domain.port.out.server_services;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainServiceServices {
    List<ServerServicesEntity> findByServerId(Long serverId, String transactionId);

    Optional<ServerServicesEntity> findByNameAndServerId(String name, Long serverId, String transactionId);

    List<ServerServicesEntity> findByServersAvailableRequestLoa(String transactionId);

    Optional<ServerServicesEntity> findById(Long id);
}
