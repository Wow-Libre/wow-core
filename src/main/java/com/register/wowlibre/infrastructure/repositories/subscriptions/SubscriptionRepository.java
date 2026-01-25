package com.register.wowlibre.infrastructure.repositories.subscriptions;

import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByReferenceNumber(String referenceNumber);
    List<SubscriptionEntity> findByUserId(Long userId);
    Optional<SubscriptionEntity> findByUserIdAndStatus(Long userId, String status);
    List<SubscriptionEntity> findByStatus(String status);
    List<SubscriptionEntity> findByNextInvoiceDateBeforeAndStatus(LocalDate date, String status);
}
