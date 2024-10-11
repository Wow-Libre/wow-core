package com.register.wowlibre.domain.port.out.server_services;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainServiceServices {
    List<ServerServicesEntity> findByServerId(Long serverId, String transactionId);

    Optional<ServerServicesEntity> findByName(String name, String transactionId);
}
