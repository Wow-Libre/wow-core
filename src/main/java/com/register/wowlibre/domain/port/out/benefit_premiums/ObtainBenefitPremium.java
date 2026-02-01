package com.register.wowlibre.domain.port.out.benefit_premiums;

import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumEntity;

import java.util.List;
import java.util.Optional;

public interface ObtainBenefitPremium {
  List<BenefitPremiumEntity> findByStatusIsTrue();

  List<BenefitPremiumEntity> findByRealmIdAndLanguageAndStatusIsTrue(Long realmId, String language, String transactionId);

  Optional<BenefitPremiumEntity> findById(Long id);

  List<BenefitPremiumEntity> findByRealmIdAndStatusIsTrue(Long realmId);
}
