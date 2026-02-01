package com.register.wowlibre.infrastructure.telegram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotProperties {

    private String token = "";
    private String username = "";
    /** Si false, el bot no se registra (evita 404 en prod si el token no es válido). Por defecto true. */
    private boolean enabled = true;
    private RateLimit rateLimit = new RateLimit();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token != null ? token : "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username : "";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit != null ? rateLimit : new RateLimit();
    }

    public boolean isConfigured() {
        return token != null && !token.isBlank();
    }

    /** Límite de mensajes por ventana de tiempo por chat (anti-spam). */
    public static class RateLimit {
        private int maxPerWindow = 0;
        private int windowSeconds = 0;

        public int getMaxPerWindow() {
            return maxPerWindow;
        }

        public void setMaxPerWindow(int maxPerWindow) {
            this.maxPerWindow = maxPerWindow;
        }

        public int getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(int windowSeconds) {
            this.windowSeconds = windowSeconds;
        }
    }
}
