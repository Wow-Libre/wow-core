package com.register.wowlibre.infrastructure.telegram.config;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * El bot de Telegram solo se registra cuando {@code telegram.bot.token} está definido y no está vacío.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(TelegramBotEnabledCondition.OnProperty.class)
public @interface TelegramBotEnabledCondition {

    class OnProperty implements org.springframework.context.annotation.Condition {
        @Override
        public boolean matches(org.springframework.context.annotation.ConditionContext context,
                              org.springframework.core.type.AnnotatedTypeMetadata metadata) {
            String token = context.getEnvironment().getProperty("telegram.bot.token");
            return token != null && !token.isBlank();
        }
    }
}
