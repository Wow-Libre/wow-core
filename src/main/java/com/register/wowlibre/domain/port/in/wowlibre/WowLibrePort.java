package com.register.wowlibre.domain.port.in.wowlibre;

import com.register.wowlibre.domain.model.ItemQuantityModel;
import java.util.List;

public interface WowLibrePort {

  void sendPurchases(Long realmId, Long userId, Long accountId, Double gold, List<ItemQuantityModel> items,
                     String reference, String transactionId);

  void sendBenefitsPremium(Long realmId, Long userId, Long accountId,
                           Long characterId, List<ItemQuantityModel> items,
                           String benefitType, Double amount, String transactionId);
}
