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
 * si no, muestra "Login". Siempre muestra Realmlist y Trivia.
 */
@Component
@Order(20)
public class MenuHandler implements TelegramCommandHandler {

    public static final String MENU_LOGIN = "Login";
    public static final String MENU_LOGOUT = "Desconectarse";
    public static final String MENU_REALMLIST = "Realmlist";
    public static final String MENU_TRIVIA = "Trivia";
    public static final String MENU_CREATE_QUESTION = "Crear pregunta";
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
        row2.add(new KeyboardButton(MENU_TRIVIA));
        row2.add(new KeyboardButton(MENU_CREATE_QUESTION));
        rows.add(row2);
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(MENU_MY_POINTS));
        rows.add(row3);
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

        // Login, Desconectarse, Realmlist, Trivia, Crear pregunta y Mis puntos los manejan otros handlers
        if (MENU_LOGIN.equalsIgnoreCase(text) || MENU_LOGOUT.equals(text)
                || MENU_REALMLIST.equals(text) || MENU_TRIVIA.equals(text) || MENU_CREATE_QUESTION.equals(text)
                || MENU_MY_POINTS.equals(text)) {
            return false;
        }
        if (!"/menu".equalsIgnoreCase(text)) {
            return false;
        }

        TelegramSession session = sessionStore.getOrCreate(chatId);
        String menuText = "Menú principal. Elige una opción:\n\n"
                + "• Realmlist: ver reinos disponibles.\n"
                + "• Trivia: responde preguntas (límite 10/día). +1 punto por acierto. Tras cada pregunta puedes valorar 👍/👎.\n"
                + "• Crear pregunta: escribe una pregunta de trivia (A, B, C, D). +1 punto al publicarla. Límite 10/día. Si los usuarios votan 👎, se te descontarán 2 puntos por cada voto negativo.\n"
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
