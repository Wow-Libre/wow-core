package com.register.wowlibre.infrastructure.repositories.server;

import com.register.wowlibre.domain.port.out.server.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaServerAdapter implements ObtainServerPort, SaveServerPort {
    private final ServerRepository serverRepository;


    public JpaServerAdapter(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Override
    public List<ServerEntity> findByStatusIsTrue(String transactionId) {
        return serverRepository.findByStatusIsTrue();
    }

    @Override
    public Optional<ServerEntity> findByNameAndVersionAndStatusIsTrue(String name, String version,
                                                                      String transactionId) {
        return serverRepository.findByNameAndVersionAndStatusIsTrue(name, version);
    }

    @Override
    public Optional<ServerEntity> findByNameAndVersion(String name, String version, String transactionId) {
        return serverRepository.findByNameAndVersionAndStatusIsTrue(name, version);
    }

    @Override
    public void save(ServerEntity serverEntity, String transactionId) {
        serverRepository.save(serverEntity);
    }
}

