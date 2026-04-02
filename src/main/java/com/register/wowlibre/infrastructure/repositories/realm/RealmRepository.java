package com.register.wowlibre.infrastructure.repositories.realm;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface RealmRepository extends CrudRepository<RealmEntity, Long> {
    List<RealmEntity> findByStatusIsTrue();

    List<RealmEntity> findByStatusIsTrueAndShowInGameRegistrationIsTrue();

    @Override
    List<RealmEntity> findAll();

    Optional<RealmEntity> findByNameAndExpansionIdAndStatusIsTrue(String name, Integer expansionId);

    Optional<RealmEntity> findByNameAndExpansionId(String name, Integer expansionId);

    Optional<RealmEntity> findByApiKey(String apiKey);

}
