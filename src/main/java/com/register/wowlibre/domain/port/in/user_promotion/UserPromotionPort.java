package com.register.wowlibre.domain.port.in.user_promotion;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface UserPromotionPort {
    void save(Long userId, Long accountId, Long promotionId, String transactionId);
    Optional<UserPromotionEntity> findByUserIdAndAccountId(Long userId, Long accountId, Long promotionId,
                                                           String transactionId);
}
