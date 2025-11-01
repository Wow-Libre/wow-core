package com.register.wowlibre.domain.port.out.promotion;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainPromotion {

    List<PromotionEntity> findByPromotionRealmIdAndLanguage(Long realmId, String language, String transactionId);

    Optional<PromotionEntity> findById(Long id, String transactionId);

    List<PromotionEntity> findByPromotionRealmId(Long realmId, String transactionId);

    List<PromotionEntity> findActiveByRealmId(Long realmId, String language, String transactionId);

}
