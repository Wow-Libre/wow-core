package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.domain.port.in.subscriptions.SubscriptionPort;
import com.register.wowlibre.infrastructure.entities.transactions.PlansEntity;
import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionEntity;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSession;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSessionStore;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.format.DateTimeFormatter;

/**
 * Consulta de suscripción tras login: estado, plan, periodicidad y próxima renovación.
 */
@Component
@Order(6)
public class SubscriptionTelegramHandler implements TelegramCommandHandler {

    public static final String MENU_SUBSCRIPTION = "📋 Suscripción";

    private static final String CMD_SUBSCRIPTION = "/subscription";
    private static final String MSG_LOGIN_FIRST = "Haz login para ver los datos de tu suscripción.";
    private static final String TRANSACTION_ID = "[TelegramBot][Subscription]";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TelegramSessionStore sessionStore;
    private final SubscriptionPort subscriptionPort;

    public SubscriptionTelegramHandler(TelegramSessionStore sessionStore, SubscriptionPort subscriptionPort) {
        this.sessionStore = sessionStore;
        this.subscriptionPort = subscriptionPort;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        String text = update.getMessage().getText().trim();
        if (!CMD_SUBSCRIPTION.equalsIgnoreCase(text) && !MENU_SUBSCRIPTION.equals(text)) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        TelegramSession session = sessionStore.getOrCreate(chatId);
        if (session.getUserId() == null) {
            sendText(client, chatId, MSG_LOGIN_FIRST);
            return true;
        }
        var subOpt = subscriptionPort.findActiveSubscription(session.getUserId(), TRANSACTION_ID);
        if (subOpt.isEmpty()) {
            sendText(client, chatId, """
                    No tienes suscripción activa en este momento.
                    Para contratar o renovar, usa la web de WowLibre.
                    """);
            return true;
        }
        sendText(client, chatId, formatSubscriptionDetails(subOpt.get()));
        return true;
    }

    private static String formatSubscriptionDetails(SubscriptionEntity sub) {
        PlansEntity plan = sub.getPlanId();
        String planName = plan != null && plan.getName() != null ? plan.getName() : "—";
        String priceTitle = plan != null && plan.getPriceTitle() != null ? plan.getPriceTitle() : "";
        String currency = plan != null && plan.getCurrency() != null ? plan.getCurrency() : "";
        String freq = formatFrequency(plan);
        String next = sub.getNextInvoiceDate() != null ? sub.getNextInvoiceDate().format(DATE_FMT) : "no informada (revisa en la web)";
        String alta = sub.getCreatedAt() != null ? sub.getCreatedAt().format(DATE_TIME_FMT) : "—";
        String ref = sub.getReferenceNumber() != null ? sub.getReferenceNumber() : "—";
        String estado = sub.getStatus() != null ? sub.getStatus() : "ACTIVE";

        StringBuilder sb = new StringBuilder();
        sb.append("📋 Tu suscripción WowLibre\n\n");
        sb.append("Estado: ").append(estado).append("\n");
        sb.append("Plan: ").append(planName).append("\n");
        if (!priceTitle.isBlank()) {
            sb.append("Resumen de precio: ").append(priceTitle);
            if (!currency.isBlank()) {
                sb.append(" (").append(currency).append(")");
            }
            sb.append("\n");
        }
        if (!freq.isBlank()) {
            sb.append("Periodicidad: ").append(freq).append("\n");
        }
        sb.append("Próxima renovación / facturación: ").append(next).append("\n");
        sb.append("Alta: ").append(alta).append("\n");
        sb.append("Referencia: ").append(ref).append("\n\n");
        sb.append("Cambios de plan o pagos: desde la web.");
        return sb.toString();
    }

    private static String formatFrequency(PlansEntity plan) {
        if (plan == null) {
            return "";
        }
        Integer v = plan.getFrequencyValue();
        String t = plan.getFrequencyType();
        if (v == null || t == null || t.isBlank()) {
            return "";
        }
        String tipo = t.equalsIgnoreCase("YEARLY") ? "año(s)" : t.equalsIgnoreCase("MONTHLY") ? "mes(es)" : t;
        return "cada " + v + " " + tipo;
    }

    private static void sendText(TelegramClient client, long chatId, String text) {
        try {
            client.execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (Exception ignored) {
        }
    }
}
