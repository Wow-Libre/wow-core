package com.register.wowlibre.infrastructure.repositories.benefit_premiums;

import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BenefitPremiumRepository extends CrudRepository<BenefitPremiumEntity, Long> {
    List<BenefitPremiumEntity> findByStatusTrue();
    List<BenefitPremiumEntity> findByRealmIdAndStatusTrue(Long realmId);
    List<BenefitPremiumEntity> findByRealmIdAndStatusTrueAndLanguage(Long realmId, String language);
}
