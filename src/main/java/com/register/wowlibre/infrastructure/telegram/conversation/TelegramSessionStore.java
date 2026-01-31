package com.register.wowlibre.infrastructure.telegram.conversation;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacén en memoria de sesiones por chat (chatId).
 * Mantiene el estado del flujo de login (email pendiente, etc.).
 */
@Component
public class TelegramSessionStore {

    private final Map<Long, TelegramSession> sessions = new ConcurrentHashMap<>();

    public TelegramSession getOrCreate(Long chatId) {
        return sessions.computeIfAbsent(chatId, k -> new TelegramSession());
    }

    public void clear(Long chatId) {
        TelegramSession session = sessions.get(chatId);
        if (session != null) {
            session.clear();
        }
    }
}
