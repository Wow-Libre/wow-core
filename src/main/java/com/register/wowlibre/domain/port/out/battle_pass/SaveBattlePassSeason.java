package com.register.wowlibre.domain.port.out.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassSeasonEntity;

public interface SaveBattlePassSeason {

    BattlePassSeasonEntity save(BattlePassSeasonEntity entity);
}
