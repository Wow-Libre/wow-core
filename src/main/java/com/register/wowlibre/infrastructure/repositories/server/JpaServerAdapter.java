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
    public Optional<ServerEntity> findByNameAndExpansionAndStatusIsTrue(String name, String version,
                                                                      String transactionId) {
        return serverRepository.findByNameAndExpansionAndStatusIsTrue(name, version);
    }

    @Override
    public Optional<ServerEntity> findByApiKeyAndStatusIsTrue(String apikey, String transactionId) {
        return serverRepository.findByApiKeyAndStatusIsTrue(apikey);
    }

    @Override
    public Optional<ServerEntity> findByNameAndExpansion(String name, String expansion, String transactionId) {
        return serverRepository.findByNameAndExpansionAndStatusIsTrue(name, expansion);
    }

    @Override
    public void save(ServerEntity serverEntity, String transactionId) {
        serverRepository.save(serverEntity);
    }
}

