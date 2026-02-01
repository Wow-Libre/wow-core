package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.domain.dto.RealmDto;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

/**
 * Devuelve la lista de reinos disponibles con su realmlist para conectar el cliente WoW.
 */
@Component
@Order(15)
public class RealmlistHandler implements TelegramCommandHandler {

    public static final String MENU_REALMLIST = "Realmlist";

    private static final String CMD_REALMLIST = "/realmlist";
    private static final String MSG_HEADER = "Reinos disponibles:\n\n";
    private static final String MSG_EMPTY = "No hay reinos disponibles en este momento.";
    private static final String FORMAT_REALM = "• %s (%s)\n  set realmlist %s\n";

    private final RealmPort realmPort;

    public RealmlistHandler(RealmPort realmPort) {
        this.realmPort = realmPort;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        String text = update.getMessage().getText().trim();
        if (!CMD_REALMLIST.equalsIgnoreCase(text) && !MENU_REALMLIST.equals(text)) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        String transactionId = "[TelegramBot][Realmlist]";
        List<RealmDto> realms = realmPort.findByStatusIsTrue(transactionId);
        String message = buildRealmlistMessage(realms);
        try {
            client.execute(SendMessage.builder().chatId(chatId).text(message).build());
        } catch (Exception e) {
            // log
        }
        return true;
    }

    private static String buildRealmlistMessage(List<RealmDto> realms) {
        if (realms == null || realms.isEmpty()) {
            return MSG_EMPTY;
        }
        StringBuilder sb = new StringBuilder(MSG_HEADER);
        for (RealmDto r : realms) {
            String name = r.getName() != null ? r.getName() : "Reino";
            String expName = r.getExpName() != null ? r.getExpName() : "";
            String realmlist = r.getRealmlist() != null && !r.getRealmlist().isBlank()
                    ? r.getRealmlist()
                    : "(no configurado)";
            sb.append(String.format(FORMAT_REALM, name, expName, realmlist));
        }
        return sb.toString();
    }
}
