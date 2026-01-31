package com.register.wowlibre.infrastructure.telegram;

import com.register.wowlibre.infrastructure.telegram.handler.TelegramCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

/**
 * Despacha cada update al primer handler que lo procese.
 * Los handlers se inyectan ordenados por @Order (menor = mayor prioridad).
 */
@Component
public class TelegramUpdateDispatcher {

    private static final Logger log = LoggerFactory.getLogger(TelegramUpdateDispatcher.class);

    private final List<TelegramCommandHandler> handlers;

    public TelegramUpdateDispatcher(List<TelegramCommandHandler> handlers) {
        this.handlers = handlers != null ? handlers : List.of();
    }

    public void dispatch(Update update, TelegramClient client) {
        for (TelegramCommandHandler handler : handlers) {
            try {
                if (handler.handle(update, client)) {
                    return;
                }
            } catch (Exception e) {
                log.warn("Handler {} failed for update: {}", handler.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
