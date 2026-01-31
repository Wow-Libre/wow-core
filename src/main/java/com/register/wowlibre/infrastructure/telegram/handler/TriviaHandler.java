package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.domain.dto.TriviaQuestionDto;
import com.register.wowlibre.domain.port.in.trivia.TriviaPort;
import com.register.wowlibre.domain.port.in.wallet.WalletPort;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramConversationState;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSession;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSessionStore;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;

/**
 * Minijuego Trivia: una pregunta con opciones A, B, C, D.
 * Límite de 10 preguntas diarias por usuario. Cada respuesta correcta da +1 punto en la Wallet.
 */
@Component
@Order(6)
public class TriviaHandler implements TelegramCommandHandler {

    public static final String MENU_TRIVIA = "Trivia";

    private static final String CMD_TRIVIA = "/trivia";
    private static final String MSG_LOGIN_FIRST = "Haz login y elige reino para jugar a la trivia.";
    private static final String MSG_DAILY_LIMIT = "Has alcanzado el límite de 10 preguntas diarias (0 restantes). Vuelve mañana.";
    private static final String MSG_NO_QUESTIONS = "No hay preguntas disponibles en este momento.";
    private static final String MSG_CORRECT = "¡Correcto! +1 punto añadido a tu billetera.";
    private static final String MSG_WRONG = "Incorrecto. La respuesta correcta era %s.";
    private static final String MSG_CANCEL = "Trivia cancelada.";
    private static final String FORMAT_QUESTION = "%s\n\nA) %s\nB) %s\nC) %s\nD) %s\n\nResponde con A, B, C o D.";
    private static final String SUFFIX_REMAINING = "\n\n📋 Te quedan %d preguntas hoy (de 10).";
    private static final String SUFFIX_REMAINING_AFTER = " Te quedan %d preguntas hoy (de 10).";
    private static final String MSG_RATE_QUESTION = "¿Te gustó esta pregunta? Responde 👍 (sí) o 👎 (no).";
    private static final String MSG_THANKS_RATING = "Gracias por tu valoración.";
    private static final String TRANSACTION_ID = "[TelegramBot][Trivia]";

    private final TelegramSessionStore sessionStore;
    private final TriviaPort triviaPort;
    private final WalletPort walletPort;

    public TriviaHandler(TelegramSessionStore sessionStore, TriviaPort triviaPort, WalletPort walletPort) {
        this.sessionStore = sessionStore;
        this.triviaPort = triviaPort;
        this.walletPort = walletPort;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        TelegramSession session = sessionStore.getOrCreate(chatId);

        // Flujo en curso: esperando valoración 👍/👎
        if (session.getState() == TelegramConversationState.AWAITING_TRIVIA_RATING) {
            if (isCancel(text)) {
                session.clearConversationOnly();
                sendText(client, chatId, MSG_CANCEL);
                return true;
            }
            return handleTriviaRating(chatId, text, session, client);
        }

        // Flujo en curso: esperando respuesta A/B/C/D
        if (session.getState() == TelegramConversationState.AWAITING_TRIVIA_ANSWER) {
            if (isCancel(text)) {
                session.clearConversationOnly();
                sendText(client, chatId, MSG_CANCEL);
                return true;
            }
            return handleTriviaAnswer(chatId, text, session, client);
        }

        // Iniciar trivia
        if (CMD_TRIVIA.equalsIgnoreCase(text) || MENU_TRIVIA.equals(text)) {
            if (session.getUserId() == null) {
                sendText(client, chatId, MSG_LOGIN_FIRST);
                return true;
            }
            if (!triviaPort.canAskQuestionToday(session.getUserId(), TRANSACTION_ID)) {
                sendText(client, chatId, MSG_DAILY_LIMIT);
                return true;
            }
            Optional<TriviaQuestionDto> questionOpt = triviaPort.getRandomQuestion(TRANSACTION_ID);
            if (questionOpt.isEmpty()) {
                sendText(client, chatId, MSG_NO_QUESTIONS);
                return true;
            }
            TriviaQuestionDto q = questionOpt.get();
            triviaPort.recordQuestionAsked(session.getUserId(), TRANSACTION_ID);
            int remaining = triviaPort.getRemainingQuestionsToday(session.getUserId(), TRANSACTION_ID);
            String questionText = String.format(FORMAT_QUESTION,
                    q.getQuestionText(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD());
            String message = questionText + String.format(SUFFIX_REMAINING, remaining);
            session.setTriviaCorrectOption(normalizeOption(q.getCorrectOption()));
            session.setTriviaQuestionId(q.getId());
            session.setState(TelegramConversationState.AWAITING_TRIVIA_ANSWER);
            sendText(client, chatId, message);
            return true;
        }

        return false;
    }

    private boolean handleTriviaAnswer(long chatId, String text, TelegramSession session, TelegramClient client) {
        String answer = normalizeOption(text);
        String correct = session.getTriviaCorrectOption();
        int remaining = triviaPort.getRemainingQuestionsToday(session.getUserId(), TRANSACTION_ID);
        String remainingSuffix = String.format(SUFFIX_REMAINING_AFTER, remaining);

        if (correct != null && correct.equals(answer)) {
            walletPort.addPoints(session.getUserId(), 1L, TRANSACTION_ID);
            sendText(client, chatId, MSG_CORRECT + remainingSuffix);
        } else {
            sendText(client, chatId, String.format(MSG_WRONG, correct != null ? correct : "?") + remainingSuffix);
        }

        Long questionId = session.getTriviaQuestionId();
        if (questionId != null) {
            if (triviaPort.hasRatedQuestion(questionId, session.getUserId(), TRANSACTION_ID)) {
                session.clearConversationOnly();
            } else {
                session.setState(TelegramConversationState.AWAITING_TRIVIA_RATING);
                sendText(client, chatId, MSG_RATE_QUESTION);
            }
        } else {
            session.clearConversationOnly();
        }
        return true;
    }

    private boolean handleTriviaRating(long chatId, String text, TelegramSession session, TelegramClient client) {
        Long questionId = session.getTriviaQuestionId();
        session.clearConversationOnly();
        boolean isPositive = parseRating(text);
        if (questionId != null) {
            triviaPort.rateQuestion(questionId, session.getUserId(), isPositive, TRANSACTION_ID);
        }
        int remaining = triviaPort.getRemainingQuestionsToday(session.getUserId(), TRANSACTION_ID);
        sendText(client, chatId, MSG_THANKS_RATING + String.format(SUFFIX_REMAINING_AFTER, remaining));
        return true;
    }

    private static boolean parseRating(String text) {
        if (text == null) return true;
        String t = text.trim().toLowerCase();
        if (t.contains("👍") || "si".equals(t) || "sí".equals(t) || "1".equals(t) || "yes".equals(t)) return true;
        if (t.contains("👎") || "no".equals(t) || "2".equals(t)) return false;
        return true;
    }

    private static String normalizeOption(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim().toUpperCase();
        if (t.length() >= 1) {
            char c = t.charAt(0);
            if (c == '1') return "A";
            if (c == '2') return "B";
            if (c == '3') return "C";
            if (c == '4') return "D";
            return String.valueOf(c);
        }
        return null;
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
