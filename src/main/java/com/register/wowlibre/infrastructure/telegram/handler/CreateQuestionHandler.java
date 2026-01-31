package com.register.wowlibre.infrastructure.telegram.handler;

import com.register.wowlibre.domain.port.in.trivia.TriviaPort;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramConversationState;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSession;
import com.register.wowlibre.infrastructure.telegram.conversation.TelegramSessionStore;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Flujo para que el usuario cree una pregunta de trivia.
 * Gana +1 punto al crear; si recibe votos negativos se le descontarán 2 puntos.
 */
@Component
@Order(5)
public class CreateQuestionHandler implements TelegramCommandHandler {

    public static final String MENU_CREATE_QUESTION = "Crear pregunta";

    private static final String CMD_CREATE = "/crear_pregunta";
    private static final String MSG_LOGIN_FIRST = "Haz login para crear preguntas.";
    private static final String MSG_DAILY_CREATE_LIMIT = "Has alcanzado el límite de 10 preguntas creadas hoy (0 restantes). Vuelve mañana.";
    private static final String MSG_CREATE_DYNAMICS = "📝 Crear pregunta: escribirás el enunciado y las 4 opciones (A–D). Al publicarla ganas +1 punto. Si otros usuarios votan 👎 tu pregunta, se te descontarán 2 puntos por cada voto negativo. Límite: 10 preguntas creadas por día.";
    private static final String MSG_QUESTION_TEXT = "Escribe el texto de la pregunta (máx. 500 caracteres):";
    private static final String MSG_OPTION_A = "Escribe la opción A:";
    private static final String MSG_OPTION_B = "Escribe la opción B:";
    private static final String MSG_OPTION_C = "Escribe la opción C:";
    private static final String MSG_OPTION_D = "Escribe la opción D:";
    private static final String MSG_CORRECT = "Indica la letra correcta (A, B, C o D):";
    private static final String MSG_SUCCESS = "Pregunta creada. +1 punto añadido a tu billetera.\n\n⚠️ Si los usuarios votan negativamente (👎) esta pregunta, se te descontarán 2 puntos.";
    private static final String SUFFIX_REMAINING_CREATES = "\n\n📋 Te quedan %d preguntas por crear hoy (de 10).";
    private static final String MSG_CANCEL = "Creación de pregunta cancelada.";
    private static final String MSG_INVALID_CORRECT = "Escribe solo A, B, C o D.";
    private static final String TRANSACTION_ID = "[TelegramBot][CrearPregunta]";

    private final TelegramSessionStore sessionStore;
    private final TriviaPort triviaPort;

    public CreateQuestionHandler(TelegramSessionStore sessionStore, TriviaPort triviaPort) {
        this.sessionStore = sessionStore;
        this.triviaPort = triviaPort;
    }

    @Override
    public boolean handle(Update update, TelegramClient client) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        TelegramSession session = sessionStore.getOrCreate(chatId);

        // Flujo en curso: crear pregunta paso a paso
        if (isCreateQuestionState(session.getState())) {
            if (isCancel(text)) {
                session.clearConversationOnly();
                sendText(client, chatId, MSG_CANCEL);
                return true;
            }
            return handleCreateQuestionStep(chatId, text, session, client);
        }

        // Iniciar flujo crear pregunta
        if (CMD_CREATE.equalsIgnoreCase(text) || MENU_CREATE_QUESTION.equals(text)) {
            if (session.getUserId() == null) {
                sendText(client, chatId, MSG_LOGIN_FIRST);
                return true;
            }
            if (!triviaPort.canCreateQuestionToday(session.getUserId(), TRANSACTION_ID)) {
                sendText(client, chatId, MSG_DAILY_CREATE_LIMIT);
                return true;
            }
            session.setState(TelegramConversationState.AWAITING_NEW_QUESTION_TEXT);
            sendText(client, chatId, MSG_CREATE_DYNAMICS);
            sendText(client, chatId, MSG_QUESTION_TEXT);
            return true;
        }

        return false;
    }

    private static boolean isCreateQuestionState(TelegramConversationState state) {
        return state == TelegramConversationState.AWAITING_NEW_QUESTION_TEXT
                || state == TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_A
                || state == TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_B
                || state == TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_C
                || state == TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_D
                || state == TelegramConversationState.AWAITING_NEW_QUESTION_CORRECT;
    }

    private boolean handleCreateQuestionStep(long chatId, String text, TelegramSession session, TelegramClient client) {
        switch (session.getState()) {
            case TelegramConversationState.AWAITING_NEW_QUESTION_TEXT -> {
                session.setTriviaCreateQuestionText(text.length() > 500 ? text.substring(0, 500) : text);
                session.setState(TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_A);
                sendText(client, chatId, MSG_OPTION_A);
                return true;
            }
            case TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_A -> {
                session.setTriviaCreateOptionA(cap(text, 200));
                session.setState(TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_B);
                sendText(client, chatId, MSG_OPTION_B);
                return true;
            }
            case TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_B -> {
                session.setTriviaCreateOptionB(cap(text, 200));
                session.setState(TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_C);
                sendText(client, chatId, MSG_OPTION_C);
                return true;
            }
            case TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_C -> {
                session.setTriviaCreateOptionC(cap(text, 200));
                session.setState(TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_D);
                sendText(client, chatId, MSG_OPTION_D);
                return true;
            }
            case TelegramConversationState.AWAITING_NEW_QUESTION_OPTION_D -> {
                session.setTriviaCreateOptionD(cap(text, 200));
                session.setState(TelegramConversationState.AWAITING_NEW_QUESTION_CORRECT);
                sendText(client, chatId, MSG_CORRECT);
                return true;
            }
            case TelegramConversationState.AWAITING_NEW_QUESTION_CORRECT -> {
                String letter = normalizeCorrectLetter(text);
                if (letter == null) {
                    sendText(client, chatId, MSG_INVALID_CORRECT);
                    return true;
                }
                try {
                    triviaPort.createQuestion(
                            session.getUserId(),
                            session.getTriviaCreateQuestionText(),
                            session.getTriviaCreateOptionA(),
                            session.getTriviaCreateOptionB(),
                            session.getTriviaCreateOptionC(),
                            session.getTriviaCreateOptionD(),
                            letter,
                            TRANSACTION_ID
                    );
                    session.clearConversationOnly();
                    int remaining = triviaPort.getRemainingCreatesToday(session.getUserId(), TRANSACTION_ID);
                    sendText(client, chatId, MSG_SUCCESS + String.format(SUFFIX_REMAINING_CREATES, remaining));
                } catch (IllegalStateException e) {
                    session.clearConversationOnly();
                    sendText(client, chatId, MSG_DAILY_CREATE_LIMIT);
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private static String cap(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) : s;
    }

    private static String normalizeCorrectLetter(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim().toUpperCase();
        if (t.length() >= 1) {
            char c = t.charAt(0);
            if (c == 'A' || c == 'B' || c == 'C' || c == 'D') return String.valueOf(c);
            if (c == '1') return "A";
            if (c == '2') return "B";
            if (c == '3') return "C";
            if (c == '4') return "D";
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
