package com.register.wowlibre.infrastructure.repositories.battle_pass;

import com.register.wowlibre.domain.port.out.battle_pass.ObtainBattlePassSeason;
import com.register.wowlibre.domain.port.out.battle_pass.SaveBattlePassSeason;
import com.register.wowlibre.infrastructure.entities.transactions.BattlePassSeasonEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaBattlePassSeasonAdapter implements ObtainBattlePassSeason, SaveBattlePassSeason {

    private final BattlePassSeasonRepository repository;

    public JpaBattlePassSeasonAdapter(BattlePassSeasonRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<BattlePassSeasonEntity> findActiveByRealmId(Long realmId) {
        LocalDateTime now = LocalDateTime.now();
        return repository.findFirstByRealmIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(realmId, now, now);
    }

    @Override
    public List<BattlePassSeasonEntity> findAllByRealmId(Long realmId) {
        return repository.findByRealmIdOrderByStartDateDesc(realmId);
    }

    @Override
    public Optional<BattlePassSeasonEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public BattlePassSeasonEntity save(BattlePassSeasonEntity entity) {
        return repository.save(entity);
    }
}
