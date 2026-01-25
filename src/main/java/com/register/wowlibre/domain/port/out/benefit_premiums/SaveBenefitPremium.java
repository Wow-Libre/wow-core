package com.register.wowlibre.domain.port.out.benefit_premiums;

import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumEntity;

public interface SaveBenefitPremium {
    void save(BenefitPremiumEntity benefitPremiumEntity);
}
