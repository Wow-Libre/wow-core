package com.register.wowlibre.infrastructure.repositories.subscriptions;

import com.register.wowlibre.domain.port.out.subscriptions.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Repository
public class JpaSubscriptionAdapter implements ObtainSubscription, SaveSubscription {
    private final SubscriptionRepository subscriptionRepository;

    public JpaSubscriptionAdapter(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Optional<SubscriptionEntity> findByUserIdAndStatus(Long userId, String status) {
        return subscriptionRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public Optional<SubscriptionEntity> findByReferenceNumber(String reference) {
        return subscriptionRepository.findByReferenceNumber(reference);
    }

    @Override
    public List<SubscriptionEntity> findByExpirateSubscription() {
        LocalDate today = LocalDate.now();
        return subscriptionRepository.findByNextInvoiceDateBeforeAndStatus(today, "ACTIVE");
    }

    @Override
    public List<SubscriptionEntity> findAll() {
        return (List<SubscriptionEntity>) subscriptionRepository.findAll();
    }

    @Override
    public List<SubscriptionEntity> findByActiveSubscription() {
        return subscriptionRepository.findByStatus("ACTIVE");
    }

    @Override
    public SubscriptionEntity save(SubscriptionEntity subscription, String transactionId) {
        return subscriptionRepository.save(subscription);
    }
}

