package com.register.wowlibre.application.services;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.dto.comunication.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.port.in.mail.*;
import com.register.wowlibre.domain.port.in.notification_provider.*;
import com.register.wowlibre.infrastructure.client.*;
import com.register.wowlibre.infrastructure.config.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class MailService implements MailPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private static final long REGISTER_TEMPLATE_ID = 1L;
    private final Configurations configurations;
    private final GoogleMailSend googleMailSend;
    private final NotificationProviderPort notificationProviderPort;
    private final CommunicationsClient communicationsClient;

    public MailService(Configurations configurations, GoogleMailSend googleMailSend,
                       NotificationProviderPort notificationProviderPort,
                       CommunicationsClient communicationsClient) {
        this.configurations = configurations;
        this.googleMailSend = googleMailSend;
        this.notificationProviderPort = notificationProviderPort;
        this.communicationsClient = communicationsClient;
    }


    @Override
    public void sendCodeMail(String email, String subject, String code, Locale locale, String transactionId) {
        final String url = String.format("%s/confirmation?email=%s&code=%s", configurations.getHostDomain(), email,
                code);

        Optional<NotificationProvidersEntity> provider =
                notificationProviderPort.findByName(NotificationType.MAILS.name(), transactionId);

        if (provider.isEmpty()) {
            googleMailSend.sendHTMLEmail(MailSenderVars.builder()
                    .emailFrom(email).idTemplate((int) REGISTER_TEMPLATE_ID)
                    .data(RegisterSenderVarsDto.builder().url(url).build())
                    .subject(subject).build(), transactionId);
            return;
        }

        NotificationProvidersEntity communication = provider.get();


        Map<String, String> body = new HashMap<>();
        body.put("url", url);

        communicationsClient.sendMailTemplate(communication.getHost(), communication.getClient(),
                SendMailTemplateRequest.builder()
                        .templateId(REGISTER_TEMPLATE_ID)
                        .email(email)
                        .subject(subject)
                        .body(body)
                        .secret(communication.getSecretKey())
                        .build(), transactionId);
    }

    @Override
    public void sendMail(String mail, String subject, String body, String transactionId) {

        Optional<NotificationProvidersEntity> provider =
                notificationProviderPort.findByName(NotificationType.MAILS.name(), transactionId);

        if (provider.isEmpty()) {
            googleMailSend.sendMail(mail, subject, body, transactionId);
            return;
        }

        NotificationProvidersEntity communication = provider.get();
        communicationsClient.sendMailBasic(communication.getHost(), communication.getClient(),
                new SendMailCommunicationRequest(mail, subject, body, communication.getSecretKey()), transactionId);
    }


}
