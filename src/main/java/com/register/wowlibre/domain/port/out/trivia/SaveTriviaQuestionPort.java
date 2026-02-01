package com.register.wowlibre.domain.port.out.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaQuestionEntity;

public interface SaveTriviaQuestionPort {

    TriviaQuestionEntity save(TriviaQuestionEntity entity, String transactionId);
}
