package com.register.wowlibre.infrastructure.telegram.conversation;

import lombok.Data;

@Data
public class TelegramSession {
    private TelegramConversationState state = TelegramConversationState.NONE;
    private String email;
    /** userId de la plataforma tras login exitoso */
    private Long userId;

    public void clear() {
        state = TelegramConversationState.NONE;
        email = null;
        userId = null;
    }

    /** Limpia solo el flujo de conversación, mantiene userId (sesión logueada). */
    public void clearConversationOnly() {
        state = TelegramConversationState.NONE;
        email = null;
    }
}
