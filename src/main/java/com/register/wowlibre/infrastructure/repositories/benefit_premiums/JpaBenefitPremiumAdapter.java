package com.register.wowlibre.infrastructure.repositories.benefit_premiums;

import com.register.wowlibre.domain.port.out.benefit_premiums.ObtainBenefitPremium;
import com.register.wowlibre.domain.port.out.benefit_premiums.SaveBenefitPremium;
import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBenefitPremiumAdapter implements ObtainBenefitPremium, SaveBenefitPremium {
    private final BenefitPremiumRepository benefitPremiumRepository;

    public JpaBenefitPremiumAdapter(BenefitPremiumRepository benefitPremiumRepository) {
        this.benefitPremiumRepository = benefitPremiumRepository;
    }

    @Override
    public List<BenefitPremiumEntity> findByStatusIsTrue() {
        return benefitPremiumRepository.findByStatusTrue();
    }

    @Override
    public List<BenefitPremiumEntity> findByRealmIdAndLanguageAndStatusIsTrue(Long realmId, String language, String transactionId) {
        return benefitPremiumRepository.findByRealmIdAndStatusTrueAndLanguage(realmId, language);
    }

    @Override
    public Optional<BenefitPremiumEntity> findById(Long id) {
        return benefitPremiumRepository.findById(id);
    }

    @Override
    public List<BenefitPremiumEntity> findByRealmIdAndStatusIsTrue(Long realmId) {
        return benefitPremiumRepository.findByRealmIdAndStatusTrue(realmId);
    }

    @Override
    public void save(BenefitPremiumEntity benefitPremiumEntity) {
        benefitPremiumRepository.save(benefitPremiumEntity);
    }
}
