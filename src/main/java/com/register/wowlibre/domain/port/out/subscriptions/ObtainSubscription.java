package com.register.wowlibre.domain.port.out.subscriptions;

import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionEntity;

import java.util.*;

public interface ObtainSubscription {
    Optional<SubscriptionEntity> findByUserIdAndStatus(Long userId, String status);

    Optional<SubscriptionEntity> findByReferenceNumber(String reference);

    List<SubscriptionEntity> findByExpirateSubscription();

    List<SubscriptionEntity> findAll();
}
