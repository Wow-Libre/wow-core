package com.register.wowlibre.domain.port.out.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaDailyCreateEntity;

public interface SaveTriviaDailyCreatePort {

    void save(TriviaDailyCreateEntity entity, String transactionId);
}
