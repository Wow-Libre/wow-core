package com.register.wowlibre.infrastructure.repositories.benefit_premium_items;

import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumItemEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BenefitPremiumItemRepository extends CrudRepository<BenefitPremiumItemEntity, Long> {
    List<BenefitPremiumItemEntity> findByBenefitPremium_Id(Long benefitPremiumId);
}
