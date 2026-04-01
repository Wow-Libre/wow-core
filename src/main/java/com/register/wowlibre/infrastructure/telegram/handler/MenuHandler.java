package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSession;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSessionStore;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Muestra el menú principal. Si el usuario está logueado muestra "Desconectarse";
 * si no, muestra "Login". Incluye Realmlist, suscripción y puntos.
 */
@Component
@Order(20)
public class MenuHandler implements TelegramCommandHandler {

    public static final String MENU_LOGIN = "Login";
    public static final String MENU_LOGOUT = "Desconectarse";
    public static final String MENU_REALMLIST = "Realmlist";
    /** Opción llamativa para ver el saldo de puntos */
    public static final String MENU_MY_POINTS = "💰 Mis puntos";

    private final TelegramSessionStore sessionStore;

    public MenuHandler(TelegramSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    /**
     * Teclado del menú según el estado de la sesión: logueado → "Desconectarse"; no logueado → "Login".
     */
    public static ReplyKeyboardMarkup buildMainMenuKeyboard(TelegramSession session) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        if (session != null && session.getUserId() != null) {
            row1.add(new KeyboardButton(MENU_LOGOUT));
        } else {
            row1.add(new KeyboardButton(MENU_LOGIN));
        }
        row1.add(new KeyboardButton(MENU_REALMLIST));
        rows.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(SubscriptionTelegramHandler.MENU_SUBSCRIPTION));
        row2.add(new KeyboardButton(MENU_MY_POINTS));
        rows.add(row2);
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(rows);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        return keyboard;
    }

    /**
     * Aplica el teclado del menú a un mensaje (requiere sesión para mostrar Login o Desconectarse).
     */
    public static void attachMenu(SendMessage.SendMessageBuilder builder, TelegramSession session) {
        builder.replyMarkup(buildMainMenuKeyboard(session));
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        if (MENU_LOGIN.equalsIgnoreCase(text) || MENU_LOGOUT.equals(text)
                || MENU_REALMLIST.equals(text) || SubscriptionTelegramHandler.MENU_SUBSCRIPTION.equals(text)
                || MENU_MY_POINTS.equals(text)) {
            return false;
        }
        if (!"/menu".equalsIgnoreCase(text)) {
            return false;
        }

        TelegramSession session = sessionStore.getOrCreate(chatId);
        String menuText = "Menú principal. Elige una opción:\n\n"
                + "• Realmlist: ver reinos disponibles.\n"
                + "• 📋 Suscripción: tras hacer login, ver si tu suscripción está activa, plan y próxima renovación.\n"
                + "• 💰 Mis puntos: ver tu saldo de puntos.";
        SendMessage.SendMessageBuilder builder = SendMessage.builder()
                .chatId(chatId)
                .text(menuText);
        attachMenu(builder, session);
        try {
            client.execute(builder.build());
        } catch (Exception e) {
            // log
        }
        return true;
    }
}
