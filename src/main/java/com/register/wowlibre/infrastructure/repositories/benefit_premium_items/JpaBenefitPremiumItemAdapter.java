package com.register.wowlibre.infrastructure.repositories.benefit_premium_items;

import com.register.wowlibre.domain.port.out.benefit_premium_items.ObtainBenefitPremiumItem;
import com.register.wowlibre.domain.port.out.benefit_premium_items.SaveBenefitPremiumItem;
import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumItemEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaBenefitPremiumItemAdapter implements ObtainBenefitPremiumItem, SaveBenefitPremiumItem {
    private final BenefitPremiumItemRepository benefitPremiumItemRepository;

    public JpaBenefitPremiumItemAdapter(BenefitPremiumItemRepository benefitPremiumItemRepository) {
        this.benefitPremiumItemRepository = benefitPremiumItemRepository;
    }

    @Override
    public List<BenefitPremiumItemEntity> findByBenefitPremiumId(Long benefitPremiumId, String transactionId) {
        return benefitPremiumItemRepository.findByBenefitPremium_Id(benefitPremiumId);
    }

    @Override
    public BenefitPremiumItemEntity save(BenefitPremiumItemEntity benefitPremiumItemEntity) {
        return benefitPremiumItemRepository.save(benefitPremiumItemEntity);
    }

    @Override
    public void delete(BenefitPremiumItemEntity benefitPremiumItemEntity) {
        benefitPremiumItemRepository.delete(benefitPremiumItemEntity);
    }
}
