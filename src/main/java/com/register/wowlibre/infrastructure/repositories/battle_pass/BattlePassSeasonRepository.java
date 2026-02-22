package com.register.wowlibre.infrastructure.repositories.battle_pass;

import com.register.wowlibre.infrastructure.entities.transactions.BattlePassSeasonEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BattlePassSeasonRepository extends CrudRepository<BattlePassSeasonEntity, Long> {

    List<BattlePassSeasonEntity> findByRealmIdOrderByStartDateDesc(Long realmId);

    Optional<BattlePassSeasonEntity> findFirstByRealmIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
            Long realmId, LocalDateTime now, LocalDateTime now2);
}
