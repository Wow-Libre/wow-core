package com.register.wowlibre.infrastructure.repositories.plans;

import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface PlansRepository extends CrudRepository<PlansEntity, Long> {

    List<PlansEntity> findByStatusIsTrue();

    List<PlansEntity> findByStatusIsTrueAndLanguage(String language);
}
