package com.register.wowlibre.domain.port.out.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaQuestionEntity;

import java.util.Optional;

public interface ObtainTriviaQuestionPort {

    /** Devuelve una pregunta activa al azar para el minijuego de trivia. */
    Optional<TriviaQuestionEntity> findRandomActive(String transactionId);

    Optional<TriviaQuestionEntity> findById(Long id, String transactionId);
}
