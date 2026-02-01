package com.register.wowlibre.infrastructure.repositories.trivia;

import com.register.wowlibre.domain.port.out.trivia.ObtainTriviaQuestionPort;
import com.register.wowlibre.domain.port.out.trivia.SaveTriviaQuestionPort;
import com.register.wowlibre.infrastructure.entities.TriviaQuestionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTriviaQuestionAdapter implements ObtainTriviaQuestionPort, SaveTriviaQuestionPort {

    private final TriviaQuestionRepository triviaQuestionRepository;

    public JpaTriviaQuestionAdapter(TriviaQuestionRepository triviaQuestionRepository) {
        this.triviaQuestionRepository = triviaQuestionRepository;
    }

    @Override
    public Optional<TriviaQuestionEntity> findRandomActive(String transactionId) {
        return triviaQuestionRepository.findRandomActive();
    }

    @Override
    public Optional<TriviaQuestionEntity> findById(Long id, String transactionId) {
        return triviaQuestionRepository.findById(id);
    }

    @Override
    public TriviaQuestionEntity save(TriviaQuestionEntity entity, String transactionId) {
        return triviaQuestionRepository.save(entity);
    }
}
