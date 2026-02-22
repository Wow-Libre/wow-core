package com.register.wowlibre.domain.port.out.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassRewardEntity;

public interface SaveBattlePassReward {

    BattlePassRewardEntity save(BattlePassRewardEntity entity);
}
