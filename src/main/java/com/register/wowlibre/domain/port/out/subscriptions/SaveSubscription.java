package com.register.wowlibre.domain.port.out.subscriptions;

import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionEntity;

public interface SaveSubscription {
    SubscriptionEntity save(SubscriptionEntity subscription, String transactionId);
}
