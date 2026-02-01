package com.register.wowlibre.domain.port.out.subscription_benefits;

import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionBenefitEntity;

import java.util.*;

public interface ObtainSubscriptionBenefits {
    Optional<SubscriptionBenefitEntity> findByUserIdAndBenefitIdAndServerId(Long userId, Long benefitId, Long serverId);
}
