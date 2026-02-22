package com.register.wowlibre.domain.port.out.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassClaimEntity;

import java.util.List;

public interface ObtainBattlePassClaim {

    List<BattlePassClaimEntity> findBySeasonAndCharacter(Long seasonId, Long realmId, Long accountId, Long characterId);

    boolean existsClaim(Long seasonId, Long realmId, Long accountId, Long characterId, Long rewardId);
}
