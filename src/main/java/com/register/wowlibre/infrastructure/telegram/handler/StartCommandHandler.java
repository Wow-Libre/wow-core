package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSession;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSessionStore;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Order(10)
public class StartCommandHandler implements TelegramCommandHandler {

    private static final String CMD_START = "/start";
    private static final String MSG_WELCOME = "Bienvenido a WowLibre. Usa el menú:\n"
            + "• Login / Realmlist / 📋 Suscripción / 💰 Mis puntos.\n"
            + "Tras el login podrás ver el detalle de tu suscripción y tus puntos.";

    private final TelegramSessionStore sessionStore;

    public StartCommandHandler(TelegramSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        String text = update.getMessage().getText().trim();
        if (!CMD_START.equalsIgnoreCase(text)) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        TelegramSession session = sessionStore.getOrCreate(chatId);
        SendMessage.SendMessageBuilder builder = SendMessage.builder()
                .chatId(chatId)
                .text(MSG_WELCOME);
        MenuHandler.attachMenu(builder, session);
        try {
            client.execute(builder.build());
        } catch (Exception e) {
            // log
        }
        return true;
    }
}
