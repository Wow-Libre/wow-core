package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.domain.port.in.wallet.WalletPort;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSession;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSessionStore;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Muestra al usuario sus puntos (billetera). Requiere estar logueado.
 */
@Component
@Order(7)
public class MyPointsHandler implements TelegramCommandHandler {

    private static final String CMD_POINTS = "/mis_puntos";
    private static final String MSG_LOGIN_FIRST = "Haz login para ver tus puntos.";
    private static final String MSG_POINTS = """
        💰 ¡TU BILLETERA WOWLIBRE!
        
        ═══════════════════════
        🏆 Tienes %d puntos
        ═══════════════════════
        
        Los puntos se acumulan según las actividades de la plataforma.
        """;
    private static final String TRANSACTION_ID = "[TelegramBot][MisPuntos]";

    private final TelegramSessionStore sessionStore;
    private final WalletPort walletPort;

    public MyPointsHandler(TelegramSessionStore sessionStore, WalletPort walletPort) {
        this.sessionStore = sessionStore;
        this.walletPort = walletPort;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        String text = update.getMessage().getText().trim();
        if (!CMD_POINTS.equalsIgnoreCase(text) && !MenuHandler.MENU_MY_POINTS.equals(text)) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        TelegramSession session = sessionStore.getOrCreate(chatId);
        if (session.getUserId() == null) {
            sendText(client, chatId, MSG_LOGIN_FIRST);
            return true;
        }
        long points = walletPort.getPoints(session.getUserId(), TRANSACTION_ID);
        String message = String.format(MSG_POINTS, points);
        sendText(client, chatId, message);
        return true;
    }

    private static void sendText(TelegramClient client, long chatId, String text) {
        try {
            client.execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (Exception ignored) {
        }
    }
}
