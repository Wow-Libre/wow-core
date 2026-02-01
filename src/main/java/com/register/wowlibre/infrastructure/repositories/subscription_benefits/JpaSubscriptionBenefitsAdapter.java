package com.register.wowlibre.infrastructure.repositories.subscription_benefits;

import com.register.wowlibre.domain.port.out.subscription_benefits.ObtainSubscriptionBenefits;
import com.register.wowlibre.domain.port.out.subscription_benefits.SaveSubscriptionBenefits;
import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionBenefitEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaSubscriptionBenefitsAdapter implements ObtainSubscriptionBenefits, SaveSubscriptionBenefits {
    private final SubscriptionBenefitsRepository subscriptionBenefitsRepository;

    public JpaSubscriptionBenefitsAdapter(SubscriptionBenefitsRepository subscriptionBenefitsRepository) {
        this.subscriptionBenefitsRepository = subscriptionBenefitsRepository;
    }

    @Override
    public Optional<SubscriptionBenefitEntity> findByUserIdAndBenefitIdAndServerId(Long userId, Long benefitId, Long serverId) {
        return subscriptionBenefitsRepository.findByUserIdAndBenefitIdAndRealmId(userId, benefitId, serverId);
    }

    @Override
    public void save(SubscriptionBenefitEntity subscriptionBenefit) {
        subscriptionBenefitsRepository.save(subscriptionBenefit);
    }
}

