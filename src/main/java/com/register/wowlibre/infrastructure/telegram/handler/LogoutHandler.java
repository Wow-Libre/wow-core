package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSession;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSessionStore;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Cierra la sesión del usuario en el bot (Desconectarse / Cerrar sesión).
 */
@Component
@Order(7)
public class LogoutHandler implements TelegramCommandHandler {

    public static final String MENU_LOGOUT = "Desconectarse";

    private static final String CMD_LOGOUT = "/logout";
    private static final String MSG_LOGGED_OUT = "Sesión cerrada. Puedes iniciar sesión de nuevo cuando quieras.";
    private static final String MSG_MENU = "Menú principal:";

    private final TelegramSessionStore sessionStore;

    public LogoutHandler(TelegramSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        if (!MENU_LOGOUT.equals(text) && !"Cerrar sesión".equals(text) && !CMD_LOGOUT.equalsIgnoreCase(text)) {
            return false;
        }

        TelegramSession session = sessionStore.getOrCreate(chatId);
        session.clear();
        sendText(client, chatId, MSG_LOGGED_OUT);
        try {
            SendMessage menuMsg = SendMessage.builder()
                    .chatId(chatId)
                    .text(MSG_MENU)
                    .replyMarkup(MenuHandler.buildMainMenuKeyboard(session))
                    .build();
            client.execute(menuMsg);
        } catch (Exception ignored) {
        }
        return true;
    }

    private static void sendText(TelegramClient client, long chatId, String text) {
        try {
            client.execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (Exception ignored) {
        }
    }
}
