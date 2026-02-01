package com.register.wowlibre.infrastructure.repositories.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaQuestionRatingEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TriviaQuestionRatingRepository extends CrudRepository<TriviaQuestionRatingEntity, Long> {

    Optional<TriviaQuestionRatingEntity> findByQuestionIdAndUserId(Long questionId, Long userId);
}
