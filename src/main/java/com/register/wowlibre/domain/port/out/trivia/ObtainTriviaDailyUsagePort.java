package com.register.wowlibre.domain.port.out.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaDailyUsageEntity;

import java.time.LocalDate;
import java.util.Optional;

public interface ObtainTriviaDailyUsagePort {

    Optional<TriviaDailyUsageEntity> findByUserIdAndUsageDate(Long userId, LocalDate usageDate);
}
