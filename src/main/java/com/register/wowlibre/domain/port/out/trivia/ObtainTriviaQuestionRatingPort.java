package com.register.wowlibre.domain.port.out.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaQuestionRatingEntity;

import java.util.Optional;

public interface ObtainTriviaQuestionRatingPort {

    Optional<TriviaQuestionRatingEntity> findByQuestionIdAndUserId(Long questionId, Long userId);
}
