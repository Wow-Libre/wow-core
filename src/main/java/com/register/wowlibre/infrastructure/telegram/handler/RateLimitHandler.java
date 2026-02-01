package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.infrastructure.telegram.ratelimit.TelegramRateLimiter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Handler de máxima prioridad (Order 0) que aplica rate limiting por chat.
 * Si un chat supera el límite de mensajes en la ventana configurada, se rechaza el mensaje
 * con un aviso y no se procesa por el resto de handlers (evita spam y ataques de consultas).
 */
@Component
@Order(0)
public class RateLimitHandler implements TelegramCommandHandler {

    private static final String MSG_RATE_LIMIT = "⏳ Por favor, no envíes tantos mensajes seguidos. Espera un momento antes de volver a escribir.";

    private final TelegramRateLimiter rateLimiter;

    public RateLimitHandler(TelegramRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        Long chatId = getChatId(update);
        if (chatId == null) {
            return false;
        }
        if (!rateLimiter.tryConsume(chatId)) {
            sendText(client, chatId, MSG_RATE_LIMIT);
            return true;
        }
        return false;
    }

    private static Long getChatId(Update update) {
        if (update.hasMessage() && update.getMessage().getChatId() != null) {
            return update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage() != null
                && update.getCallbackQuery().getMessage().getChatId() != null) {
            return (long) update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }

    private static void sendText(TelegramClient client, long chatId, String text) {
        try {
            client.execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (Exception ignored) {
        }
    }
}
