package com.register.wowlibre.domain.port.out.benefit_premium_items;

import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumItemEntity;

public interface SaveBenefitPremiumItem {
    BenefitPremiumItemEntity save(BenefitPremiumItemEntity benefitPremiumItemEntity);
    void delete(BenefitPremiumItemEntity benefitPremiumItemEntity);
}
