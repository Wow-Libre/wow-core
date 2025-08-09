package com.register.wowlibre.service;

import com.register.wowlibre.application.services.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.port.in.notification_provider.*;
import com.register.wowlibre.infrastructure.client.*;
import com.register.wowlibre.infrastructure.config.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MailServiceTest {

    private Configurations configurations;
    private MailSend mailSend;
    private NotificationProviderPort notificationProviderPort;
    private CommunicationsClient communicationsClient;
    private MailService mailService;

    @BeforeEach
    void setUp() {
        configurations = mock(Configurations.class);
        mailSend = mock(MailSend.class);
        notificationProviderPort = mock(NotificationProviderPort.class);
        communicationsClient = mock(CommunicationsClient.class);
        mailService = new MailService(configurations, mailSend, notificationProviderPort, communicationsClient);
    }

    @Test
    void sendCodeMail_providerEmpty_usesMailSend() {
        when(configurations.getHostDomain()).thenReturn("http://host");
        when(notificationProviderPort.findByName(eq(NotificationType.MAILS.name()), anyString()))
                .thenReturn(Optional.empty());

        mailService.sendCodeMail("test@mail.com", "subject", "1234", Locale.ENGLISH, "tx");

        verify(mailSend).sendHTMLEmail(any());
        verifyNoInteractions(communicationsClient);
    }

    @Test
    void sendCodeMail_providerPresent_usesCommunicationsClient() {
        NotificationProvidersEntity provider = new NotificationProvidersEntity();
        provider.setHost("host");
        provider.setClient("client");
        provider.setSecretKey("secret");
        when(configurations.getHostDomain()).thenReturn("http://host");
        when(notificationProviderPort.findByName(eq(NotificationType.MAILS.name()), anyString()))
                .thenReturn(Optional.of(provider));

        mailService.sendCodeMail("test@mail.com", "subject", "1234", Locale.ENGLISH, "tx");

        verify(communicationsClient).sendMailTemplate(eq("host"), eq("client"), any(SendMailTemplateRequest.class),
                eq("tx"));
        verifyNoInteractions(mailSend);
    }

    @Test
    void sendMail_providerEmpty_usesMailSend() {
        when(notificationProviderPort.findByName(eq(NotificationType.MAILS.name()), anyString()))
                .thenReturn(Optional.empty());

        mailService.sendMail("test@mail.com", "subject", "body", "tx");

        verify(mailSend).sendMail("test@mail.com", "subject", "body", "tx");
        verifyNoInteractions(communicationsClient);
    }

    @Test
    void sendMail_providerPresent_usesCommunicationsClient() {
        NotificationProvidersEntity provider = new NotificationProvidersEntity();
        provider.setHost("host");
        provider.setClient("client");
        provider.setSecretKey("secret");
        when(notificationProviderPort.findByName(eq(NotificationType.MAILS.name()), anyString()))
                .thenReturn(Optional.of(provider));

        mailService.sendMail("test@mail.com", "subject", "body", "tx");

        verify(communicationsClient).sendMailBasic(eq("host"), eq("client"),
                any(SendMailCommunicationRequest.class), eq("tx"));
        verifyNoInteractions(mailSend);
    }
}
