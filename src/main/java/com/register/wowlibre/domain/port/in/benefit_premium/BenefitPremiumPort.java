package com.register.wowlibre.domain.port.in.benefit_premium;

import com.register.wowlibre.domain.dto.BenefitsPremiumDto;
import com.register.wowlibre.domain.dto.CreateBenefitPremiumDto;
import java.util.List;

public interface BenefitPremiumPort {
  void createBenefitPremium(CreateBenefitPremiumDto request, String transactionId);

  void deleteBenefitPremium(Long id, String transactionId);

  List<BenefitsPremiumDto> findByLanguageAndRealmId(String language, Long realmId, String transactionId);

  List<BenefitsPremiumDto> findByRealmIdStatusIsTrue(Long realmId, String transactionId);
}
