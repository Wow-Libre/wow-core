package com.register.wowlibre.domain.port.out.server;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainServerPort {
    List<ServerEntity> findByStatusIsTrue(String transactionId);

    Optional<ServerEntity> findByNameAndExpansionAndStatusIsTrue(String name, String expansion, String transactionId);

    Optional<ServerEntity> findByApiKeyAndStatusIsTrue(String apikey, String transactionId);

    Optional<ServerEntity> findById(Long id, String transactionId);

    Optional<ServerEntity> findByNameAndExpansion(String name, String expansion, String transactionId);
}
