package com.register.wowlibre.infrastructure.repositories.battle_pass;

import com.register.wowlibre.domain.port.out.battle_pass.ObtainBattlePassClaim;
import com.register.wowlibre.domain.port.out.battle_pass.SaveBattlePassClaim;
import com.register.wowlibre.infrastructure.entities.transactions.BattlePassClaimEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaBattlePassClaimAdapter implements ObtainBattlePassClaim, SaveBattlePassClaim {

    private final BattlePassClaimRepository repository;

    public JpaBattlePassClaimAdapter(BattlePassClaimRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BattlePassClaimEntity> findBySeasonAndCharacter(Long seasonId, Long realmId, Long accountId, Long characterId) {
        return repository.findBySeasonIdAndRealmIdAndAccountIdAndCharacterId(seasonId, realmId, accountId, characterId);
    }

    @Override
    public boolean existsClaim(Long seasonId, Long realmId, Long accountId, Long characterId, Long rewardId) {
        return repository.existsBySeasonIdAndRealmIdAndAccountIdAndCharacterIdAndRewardId(
                seasonId, realmId, accountId, characterId, rewardId);
    }

    @Override
    public BattlePassClaimEntity save(BattlePassClaimEntity entity) {
        return repository.save(entity);
    }
}
