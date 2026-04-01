package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.application.services.telegram.TelegramAuthService;
import com.register.wowlibre.domain.security.JwtDto;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramConversationState;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSession;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSessionStore;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Order(5)
public class LoginHandler implements TelegramCommandHandler {

    private static final String CMD_LOGIN = "/login";
    private static final String MSG_ENTER_EMAIL = "Ingresa tu correo electrónico de la plataforma:";
    private static final String MSG_ENTER_PASSWORD = "Ingresa tu contraseña:";
    private static final String MSG_LOGIN_SUCCESS = "Sesión iniciada correctamente. Bienvenido.\n"
            + "Puedes usar 📋 Suscripción para ver tu plan y la próxima renovación.";
    private static final String MSG_LOGIN_ERROR = "Correo o contraseña incorrectos. Verifica e intenta de nuevo.";
    private static final String MSG_CANCEL = "Login cancelado.";

    private final TelegramSessionStore sessionStore;
    private final TelegramAuthService telegramAuthService;

    public LoginHandler(TelegramSessionStore sessionStore, TelegramAuthService telegramAuthService) {
        this.sessionStore = sessionStore;
        this.telegramAuthService = telegramAuthService;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        TelegramSession session = sessionStore.getOrCreate(chatId);

        // Flujo en curso: esperando email o contraseña
        if (session.getState() == TelegramConversationState.AWAITING_EMAIL) {
            return handleEmailReceived(chatId, text, session, client);
        }
        if (session.getState() == TelegramConversationState.AWAITING_PASSWORD) {
            return handlePasswordReceived(chatId, text, session, client);
        }

        // Iniciar flujo Login
        if (CMD_LOGIN.equalsIgnoreCase(text) || MenuHandler.MENU_LOGIN.equals(text)) {
            session.setState(TelegramConversationState.AWAITING_EMAIL);
            sendText(client, chatId, MSG_ENTER_EMAIL);
            return true;
        }

        return false;
    }

    private boolean handleEmailReceived(long chatId, String text, TelegramSession session, TelegramClient client) {
        if (isCancel(text)) {
            session.clear();
            sendText(client, chatId, MSG_CANCEL);
            return true;
        }
        session.setEmail(text);
        session.setState(TelegramConversationState.AWAITING_PASSWORD);
        sendText(client, chatId, MSG_ENTER_PASSWORD);
        return true;
    }

    private boolean handlePasswordReceived(long chatId, String text, TelegramSession session, TelegramClient client) {
        if (isCancel(text)) {
            session.clear();
            sendText(client, chatId, MSG_CANCEL);
            return true;
        }
        String email = session.getEmail();
        try {
            JwtDto jwt = telegramAuthService.authenticate(email, text);
            session.clearConversationOnly();
            session.setUserId(jwt.id);
            sendText(client, chatId, MSG_LOGIN_SUCCESS);
        } catch (Exception e) {
            session.clear();
            sendText(client, chatId, MSG_LOGIN_ERROR);
        }
        return true;
    }

    private static boolean isCancel(String text) {
        return "/cancel".equalsIgnoreCase(text) || "/start".equalsIgnoreCase(text)
                || "cancelar".equalsIgnoreCase(text);
    }

    private static void sendText(TelegramClient client, long chatId, String text) {
        try {
            client.execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (Exception ignored) {
        }
    }
}
