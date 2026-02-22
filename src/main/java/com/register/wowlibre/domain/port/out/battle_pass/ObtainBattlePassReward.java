package com.register.wowlibre.domain.port.out.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassRewardEntity;

import java.util.List;
import java.util.Optional;

public interface ObtainBattlePassReward {

    List<BattlePassRewardEntity> findBySeasonId(Long seasonId);

    Optional<BattlePassRewardEntity> findById(Long id);
}
