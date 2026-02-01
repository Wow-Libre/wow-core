package com.register.wowlibre.infrastructure.repositories.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaDailyCreateEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TriviaDailyCreateRepository extends CrudRepository<TriviaDailyCreateEntity, Long> {

    Optional<TriviaDailyCreateEntity> findByUserIdAndUsageDate(Long userId, LocalDate usageDate);
}
