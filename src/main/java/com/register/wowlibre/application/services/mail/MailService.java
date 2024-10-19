package com.register.wowlibre.application.services.mail;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.comunication.*;
import com.register.wowlibre.domain.port.in.mail.*;
import com.register.wowlibre.infrastructure.config.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class MailService implements MailPort {

    private final MailSend mailSend;
    private final Configurations configurations;

    public MailService(MailSend mailSend, Configurations configurations) {
        this.mailSend = mailSend;
        this.configurations = configurations;
    }

    @Async
    @Override
    public void sendCodeMail(String email, String subject, String code, Locale locale, String transactionId) {
        final String url = String.format("%s?email=%s&code=%s", configurations.getHostWowLibre(), email,
                code);

        mailSend.sendHTMLEmail(MailSenderVars.builder()
                .emailFrom(email)
                .idTemplate(1)
                .data(RegisterSenderVarsDto.builder()
                        .url(url).build())
                .subject(subject)
                .transactionId(transactionId).build());
    }


}
