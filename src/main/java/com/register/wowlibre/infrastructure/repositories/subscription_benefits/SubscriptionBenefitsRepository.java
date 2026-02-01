package com.register.wowlibre.infrastructure.repositories.subscription_benefits;

import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionBenefitEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubscriptionBenefitsRepository extends CrudRepository<SubscriptionBenefitEntity, Long> {
    Optional<SubscriptionBenefitEntity> findByUserIdAndBenefitIdAndRealmId(Long userId, Long benefitId, Long realmId);
}
