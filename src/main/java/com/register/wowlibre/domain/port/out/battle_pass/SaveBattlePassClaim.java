package com.register.wowlibre.domain.port.out.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassClaimEntity;

public interface SaveBattlePassClaim {

    BattlePassClaimEntity save(BattlePassClaimEntity entity);
}
