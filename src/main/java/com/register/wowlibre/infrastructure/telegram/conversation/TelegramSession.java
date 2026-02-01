package com.register.wowlibre.infrastructure.telegram.conversation;

import lombok.Data;

@Data
public class TelegramSession {
    private TelegramConversationState state = TelegramConversationState.NONE;
    private String email;
    /** userId de la plataforma tras login exitoso (para minijuegos) */
    private Long userId;
    /** Respuesta correcta (A, B, C o D) de la trivia en curso */
    private String triviaCorrectOption;
    /** ID de la pregunta mostrada (para valoración tras responder) */
    private Long triviaQuestionId;
    /** Flujo crear pregunta: datos temporales */
    private String triviaCreateQuestionText;
    private String triviaCreateOptionA;
    private String triviaCreateOptionB;
    private String triviaCreateOptionC;
    private String triviaCreateOptionD;

    public void clear() {
        state = TelegramConversationState.NONE;
        email = null;
        userId = null;
        triviaCorrectOption = null;
        triviaQuestionId = null;
        clearTriviaCreate();
    }

    /** Limpia solo el flujo de conversación, mantiene userId (sesión logueada). */
    public void clearConversationOnly() {
        state = TelegramConversationState.NONE;
        email = null;
        triviaCorrectOption = null;
        triviaQuestionId = null;
        clearTriviaCreate();
    }

    private void clearTriviaCreate() {
        triviaCreateQuestionText = null;
        triviaCreateOptionA = null;
        triviaCreateOptionB = null;
        triviaCreateOptionC = null;
        triviaCreateOptionD = null;
    }
}
