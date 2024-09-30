package com.register.wowlibre.domain.port.out.server;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainServerPort {
    List<ServerEntity> findByStatusIsTrue(String transactionId);

    Optional<ServerEntity> findByNameAndVersionAndStatusIsTrue(String name, String emulator, String transactionId);

    Optional<ServerEntity> findByNameAndVersion(String name, String version, String transactionId);
}
