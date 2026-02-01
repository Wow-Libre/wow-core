package com.register.wowlibre.infrastructure.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Contrato para handlers del bot.
 * Cada handler devuelve true si procesó el update; false para que el dispatcher pruebe el siguiente.
 */
public interface TelegramCommandHandler {

    /**
     * @return true si el update fue manejado; false para delegar a otro handler.
     */
    boolean handle(Update update, TelegramClient client);
}
