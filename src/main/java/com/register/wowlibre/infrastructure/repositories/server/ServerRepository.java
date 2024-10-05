package com.register.wowlibre.infrastructure.repositories.server;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface ServerRepository extends CrudRepository<ServerEntity, Long> {
    List<ServerEntity> findByStatusIsTrue();

    Optional<ServerEntity> findByNameAndExpansionAndStatusIsTrue(String name, String expansion);
    Optional<ServerEntity> findByNameAndExpansion(String name, String expansion);

    Optional<ServerEntity> findByApiKeyAndStatusIsTrue(String apiKey);

}
