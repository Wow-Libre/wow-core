package com.register.wowlibre.domain.port.out.subscription_benefits;

import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionBenefitEntity;

public interface SaveSubscriptionBenefits {
    void save(SubscriptionBenefitEntity subscriptionBenefit);
}
