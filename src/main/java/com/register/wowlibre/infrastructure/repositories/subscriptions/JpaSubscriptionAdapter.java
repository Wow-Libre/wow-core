package com.register.wowlibre.infrastructure.repositories.subscriptions;

import com.register.wowlibre.domain.port.out.subscriptions.ObtainSubscription;
import com.register.wowlibre.domain.port.out.subscriptions.SaveSubscription;
import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public SubscriptionEntity save(SubscriptionEntity subscription, String transactionId) {
        return subscriptionRepository.save(subscription);
    }
}

