package com.register.wowlibre.infrastructure.repositories.battle_pass;

import com.register.wowlibre.domain.port.out.battle_pass.DeleteBattlePassReward;
import com.register.wowlibre.domain.port.out.battle_pass.ObtainBattlePassReward;
import com.register.wowlibre.domain.port.out.battle_pass.SaveBattlePassReward;
import com.register.wowlibre.infrastructure.entities.transactions.BattlePassRewardEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBattlePassRewardAdapter implements ObtainBattlePassReward, SaveBattlePassReward, DeleteBattlePassReward {

    private final BattlePassRewardRepository repository;

    public JpaBattlePassRewardAdapter(BattlePassRewardRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BattlePassRewardEntity> findBySeasonId(Long seasonId) {
        return repository.findBySeasonIdOrderByLevelAsc(seasonId);
    }

    @Override
    public Optional<BattlePassRewardEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public BattlePassRewardEntity save(BattlePassRewardEntity entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
