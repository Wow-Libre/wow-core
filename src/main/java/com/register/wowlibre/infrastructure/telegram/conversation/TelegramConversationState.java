package com.register.wowlibre.infrastructure.telegram.conversation;

/**
 * Estado del flujo de conversación del usuario en el bot.
 */
public enum TelegramConversationState {
    NONE,
    AWAITING_EMAIL,
    AWAITING_PASSWORD
}
