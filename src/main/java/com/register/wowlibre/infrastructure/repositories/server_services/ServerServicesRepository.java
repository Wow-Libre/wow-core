package com.register.wowlibre.infrastructure.repositories.server_services;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface ServerServicesRepository extends CrudRepository<ServerServicesEntity, Long> {
    List<ServerServicesEntity> findByServerId_Id(Long serverId);

    Optional<ServerServicesEntity> findByName(String name);
}
