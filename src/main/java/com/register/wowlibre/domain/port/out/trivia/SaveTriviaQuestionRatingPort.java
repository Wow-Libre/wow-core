package com.register.wowlibre.domain.port.out.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaQuestionRatingEntity;

public interface SaveTriviaQuestionRatingPort {

    void save(TriviaQuestionRatingEntity entity, String transactionId);
}
