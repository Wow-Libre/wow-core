package com.register.wowlibre.domain.port.out.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaDailyUsageEntity;

public interface SaveTriviaDailyUsagePort {

    void save(TriviaDailyUsageEntity entity, String transactionId);
}
