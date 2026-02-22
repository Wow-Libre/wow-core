package com.register.wowlibre.domain.port.out.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassSeasonEntity;

import java.util.List;
import java.util.Optional;

public interface ObtainBattlePassSeason {

    Optional<BattlePassSeasonEntity> findActiveByRealmId(Long realmId);

    List<BattlePassSeasonEntity> findAllByRealmId(Long realmId);

    Optional<BattlePassSeasonEntity> findById(Long id);
}
