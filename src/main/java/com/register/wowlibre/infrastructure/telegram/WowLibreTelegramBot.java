package com.register.wowlibre.infrastructure.telegram;

import com.register.wowlibre.infrastructure.telegram.config.TelegramBotEnabledCondition;
import com.register.wowlibre.infrastructure.telegram.config.TelegramBotProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@TelegramBotEnabledCondition
public class WowLibreTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(WowLibreTelegramBot.class);

    private final TelegramBotProperties properties;
    private final TelegramUpdateDispatcher dispatcher;
    private final TelegramClient telegramClient;

    public WowLibreTelegramBot(TelegramBotProperties properties, TelegramUpdateDispatcher dispatcher) {
        this.properties = properties;
        this.dispatcher = dispatcher;
        this.telegramClient = new OkHttpTelegramClient(properties.getToken());
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            dispatcher.dispatch(update, telegramClient);
        } catch (Exception e) {
            log.error("Error processing telegram update: {}", e.getMessage());
        }
    }
}
