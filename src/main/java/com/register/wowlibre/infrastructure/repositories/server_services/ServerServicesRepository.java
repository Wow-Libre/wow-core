package com.register.wowlibre.infrastructure.repositories.server_services;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface ServerServicesRepository extends CrudRepository<ServerServicesEntity, Long> {
    List<ServerServicesEntity> findByServerId_Id(Long serverId);

    Optional<ServerServicesEntity> findByNameAndServerId_id(String name, Long serverId);

    @Query("SELECT ss FROM ServerServicesEntity ss " +
            "INNER JOIN ss.serverId s " +
            "WHERE ss.amount > 0 AND s.status = true")
    List<ServerServicesEntity> findActiveServerServicesWithAmountGreaterThanZero();

    @Override
    Optional<ServerServicesEntity> findById(Long id);
}
