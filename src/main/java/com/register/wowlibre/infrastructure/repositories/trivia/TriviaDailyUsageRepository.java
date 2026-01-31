package com.register.wowlibre.infrastructure.repositories.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaDailyUsageEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TriviaDailyUsageRepository extends CrudRepository<TriviaDailyUsageEntity, Long> {

    Optional<TriviaDailyUsageEntity> findByUserIdAndUsageDate(Long userId, LocalDate usageDate);
}
