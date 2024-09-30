package com.register.wowlibre.infrastructure.repositories.server;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface ServerRepository extends CrudRepository<ServerEntity, Long> {
    List<ServerEntity> findByStatusIsTrue();

    Optional<ServerEntity> findByNameAndVersionAndStatusIsTrue(String name, String version);
    Optional<ServerEntity> findByNameAndVersion(String name, String version);

    Optional<ServerEntity> findByApiKey(String apiKey);

}
