package com.register.wowlibre.domain.port.out.plans;

import com.register.wowlibre.infrastructure.entities.transactions.PlansEntity;

public interface ManagePlan {

  PlansEntity save(PlansEntity entity, String transactionId);

  void deleteById(Long id, String transactionId);
}
