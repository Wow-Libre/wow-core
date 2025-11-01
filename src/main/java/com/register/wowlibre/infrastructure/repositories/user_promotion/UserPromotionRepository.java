package com.register.wowlibre.infrastructure.repositories.user_promotion;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface UserPromotionRepository extends CrudRepository<UserPromotionEntity, Long> {

        long countByReamId(Long realmId);

        @Query("SELECT up FROM UserPromotionEntity up " +
                        "JOIN up.accountGameId ag " +
                        "WHERE ag.userId.id = :userId " +
                        "AND ag.accountId = :accountId " +
                        "AND up.promotionId = :promotionId " +
                        "AND up.characterId = :characterId")
        Optional<UserPromotionEntity> findByUserAndAccountAndPromotionAndCharacter(
                        @Param("userId") Long userId,
                        @Param("accountId") Long accountId,
                        @Param("promotionId") Long promotionId,
                        @Param("characterId") Long characterId);
}
