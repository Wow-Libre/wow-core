package com.register.wowlibre.infrastructure.repositories.trivia;

import com.register.wowlibre.domain.port.out.trivia.ObtainTriviaQuestionRatingPort;
import com.register.wowlibre.domain.port.out.trivia.SaveTriviaQuestionRatingPort;
import com.register.wowlibre.infrastructure.entities.TriviaQuestionRatingEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTriviaQuestionRatingAdapter implements ObtainTriviaQuestionRatingPort, SaveTriviaQuestionRatingPort {

    private final TriviaQuestionRatingRepository repository;

    public JpaTriviaQuestionRatingAdapter(TriviaQuestionRatingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<TriviaQuestionRatingEntity> findByQuestionIdAndUserId(Long questionId, Long userId) {
        return repository.findByQuestionIdAndUserId(questionId, userId);
    }

    @Override
    public void save(TriviaQuestionRatingEntity entity, String transactionId) {
        repository.save(entity);
    }
}
