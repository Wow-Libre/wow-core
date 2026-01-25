package com.register.wowlibre.domain.port.out.benefit_premium_items;

import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumItemEntity;

import java.util.List;

public interface ObtainBenefitPremiumItem {
    List<BenefitPremiumItemEntity> findByBenefitPremiumId(Long benefitPremiumId, String transactionId);
}
