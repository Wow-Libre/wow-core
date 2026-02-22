package com.register.wowlibre.infrastructure.repositories.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassClaimEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BattlePassClaimRepository extends CrudRepository<BattlePassClaimEntity, Long> {

    List<BattlePassClaimEntity> findBySeasonIdAndRealmIdAndAccountIdAndCharacterId(
            Long seasonId, Long realmId, Long accountId, Long characterId);

    Optional<BattlePassClaimEntity> findBySeasonIdAndRealmIdAndAccountIdAndCharacterIdAndRewardId(
            Long seasonId, Long realmId, Long accountId, Long characterId, Long rewardId);

    boolean existsBySeasonIdAndRealmIdAndAccountIdAndCharacterIdAndRewardId(
            Long seasonId, Long realmId, Long accountId, Long characterId, Long rewardId);
}
