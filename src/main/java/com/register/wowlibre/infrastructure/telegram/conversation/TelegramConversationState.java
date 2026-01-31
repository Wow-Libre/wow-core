package com.register.wowlibre.infrastructure.telegram.conversation;

/**
 * Estado del flujo de conversación del usuario en el bot.
 */
public enum TelegramConversationState {
    NONE,
    AWAITING_EMAIL,
    AWAITING_PASSWORD,
    /** Esperando respuesta A, B, C o D del usuario en el minijuego Trivia */
    AWAITING_TRIVIA_ANSWER,
    /** Esperando valoración 👍/👎 de la pregunta recién respondida */
    AWAITING_TRIVIA_RATING,
    /** Flujo crear pregunta: esperando texto de la pregunta */
    AWAITING_NEW_QUESTION_TEXT,
    /** Flujo crear pregunta: esperando opción A */
    AWAITING_NEW_QUESTION_OPTION_A,
    /** Flujo crear pregunta: esperando opción B */
    AWAITING_NEW_QUESTION_OPTION_B,
    /** Flujo crear pregunta: esperando opción C */
    AWAITING_NEW_QUESTION_OPTION_C,
    /** Flujo crear pregunta: esperando opción D */
    AWAITING_NEW_QUESTION_OPTION_D,
    /** Flujo crear pregunta: esperando letra correcta (A, B, C o D) */
    AWAITING_NEW_QUESTION_CORRECT
}
