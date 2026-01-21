package com.register.wowlibre.domain.port.out.plans;

import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface ObtainPlan {

    List<PlansEntity> findByStatusIsTrue(String transactionId);

    List<PlansEntity> findByStatusIsTrueAndLanguage(String language, String transactionId);

    Optional<PlansEntity> findById(Long planId, String transactionId);
}
