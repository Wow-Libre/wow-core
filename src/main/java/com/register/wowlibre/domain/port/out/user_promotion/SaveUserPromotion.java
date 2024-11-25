package com.register.wowlibre.domain.port.out.user_promotion;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveUserPromotion {

    void save(UserPromotionEntity userPromotionEntity, String transactionId);

}
