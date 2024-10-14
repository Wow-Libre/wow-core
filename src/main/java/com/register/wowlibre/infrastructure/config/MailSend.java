package com.register.wowlibre.infrastructure.config;

import com.mailersend.sdk.*;
import com.mailersend.sdk.emails.*;
import com.mailersend.sdk.exceptions.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

@Component
public class MailSend {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSend.class);

    private final Configurations configurations;

    public MailSend(Configurations configurations) {
        this.configurations = configurations;
    }

    public void sendMail(Email email, String transactionId) {
        MailerSend ms = new MailerSend();
        ms.setToken(configurations.getApiKeyMail());
        try {
            MailerSendResponse response = ms.emails().send(email);
            LOGGER.info(response.messageId);
        } catch (MailerSendException e) {
            LOGGER.error("The communication could not be sent. Message {} transactionId {}", e.getMessage(),
                    transactionId);
        }
    }
}
