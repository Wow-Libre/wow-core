package com.register.wowlibre.infrastructure.repositories.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassRewardEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BattlePassRewardRepository extends CrudRepository<BattlePassRewardEntity, Long> {

    List<BattlePassRewardEntity> findBySeasonIdOrderByLevelAsc(Long seasonId);
}
