package com.register.wowlibre.infrastructure.repositories.user_promotion;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface UserPromotionRepository extends CrudRepository<UserPromotionEntity, Long> {
    Optional<UserPromotionEntity> findByUserIdAndAccountIdAndPromotionId(Long userId, Long accountId, Long promotionId);
}
