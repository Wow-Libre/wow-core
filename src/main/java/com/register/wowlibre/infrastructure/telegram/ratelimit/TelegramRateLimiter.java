package com.register.wowlibre.infrastructure.telegram.ratelimit;

import com.register.wowlibre.infrastructure.telegram.config.TelegramBotProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limita la cantidad de mensajes por chat en una ventana de tiempo para evitar spam y ataques.
 * Por defecto: máximo de mensajes por minuto por chat; si se supera, se rechaza el mensaje.
 */
@Component
public class TelegramRateLimiter {

    private static final int DEFAULT_MAX_PER_WINDOW = 20;
    private static final long DEFAULT_WINDOW_MS = 60_000L; // 1 minuto

    private final int maxPerWindow;
    private final long windowMs;
    /** Por chatId: lista de timestamps de mensajes en la ventana actual. */
    private final Map<Long, List<Long>> timestampsByChat = new ConcurrentHashMap<>();
    /** Cerraduras por hash de chatId para evitar contención en un solo lock. */
    private final Object[] locks = new Object[64];

    public TelegramRateLimiter(TelegramBotProperties properties) {
        TelegramBotProperties.RateLimit rateLimit = properties.getRateLimit();
        this.maxPerWindow = rateLimit != null && rateLimit.getMaxPerWindow() > 0
                ? rateLimit.getMaxPerWindow()
                : DEFAULT_MAX_PER_WINDOW;
        this.windowMs = rateLimit != null && rateLimit.getWindowSeconds() > 0
                ? rateLimit.getWindowSeconds() * 1000L
                : DEFAULT_WINDOW_MS;
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new Object();
        }
    }

    /**
     * Intenta consumir un mensaje para este chat. Si se supera el límite, devuelve false.
     * Si se acepta, registra el mensaje y devuelve true.
     */
    public boolean tryConsume(long chatId) {
        Object lock = locks[Math.abs((int) (chatId % locks.length))];
        synchronized (lock) {
            List<Long> list = timestampsByChat.computeIfAbsent(chatId, k -> new ArrayList<>());
            long now = System.currentTimeMillis();
            list.removeIf(t -> now - t > windowMs);
            if (list.size() >= maxPerWindow) {
                return false;
            }
            list.add(now);
            return true;
        }
    }
}
