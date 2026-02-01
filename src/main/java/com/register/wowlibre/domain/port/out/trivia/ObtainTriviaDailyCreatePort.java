package com.register.wowlibre.domain.port.out.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaDailyCreateEntity;

import java.time.LocalDate;
import java.util.Optional;

public interface ObtainTriviaDailyCreatePort {

    Optional<TriviaDailyCreateEntity> findByUserIdAndUsageDate(Long userId, LocalDate usageDate);
}
