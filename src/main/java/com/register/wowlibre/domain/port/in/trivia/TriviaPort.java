package com.register.wowlibre.domain.port.in.trivia;

import com.register.wowlibre.domain.dto.TriviaQuestionDto;

import java.util.Optional;

public interface TriviaPort {

    /** Devuelve una pregunta aleatoria activa para el minijuego. */
    Optional<TriviaQuestionDto> getRandomQuestion(String transactionId);

    /** Indica si el usuario puede responder más preguntas hoy (límite diario). */
    boolean canAskQuestionToday(Long userId, String transactionId);

    /** Registra que el usuario ha respondido una pregunta hoy (incrementa el contador diario). */
    void recordQuestionAsked(Long userId, String transactionId);

    /** Preguntas que le quedan hoy al usuario (0 a 10). */
    int getRemainingQuestionsToday(Long userId, String transactionId);

    /** Indica si el usuario puede crear más preguntas hoy (límite 10/día). */
    boolean canCreateQuestionToday(Long userId, String transactionId);

    /** Preguntas que le quedan por crear hoy (0 a 10). */
    int getRemainingCreatesToday(Long userId, String transactionId);

    /** Crea una pregunta (opciones A-D y letra correcta). El creador gana +1 punto. Lanza IllegalStateException si se supera el límite diario. */
    void createQuestion(Long createdByUserId, String questionText, String optionA, String optionB,
                        String optionC, String optionD, String correctOption, String transactionId);

    /** Valoración de una pregunta por un usuario (tras responder). Solo una vez por usuario y pregunta. Si negativa, se descuentan 2 puntos al creador. */
    void rateQuestion(Long questionId, Long userId, boolean isPositive, String transactionId);

    /** Indica si el usuario ya valoró esta pregunta (para no pedir valoración de nuevo). */
    boolean hasRatedQuestion(Long questionId, Long userId, String transactionId);
}
