package com.register.wowlibre.application.services.mail;

import com.mailersend.sdk.emails.*;
import com.register.wowlibre.domain.port.in.mail.*;
import com.register.wowlibre.infrastructure.config.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class MailService implements MailPort {

    private final MailSend mailSend;

    public MailService(MailSend mailSend) {
        this.mailSend = mailSend;
    }

    @Async
    @Override
    public void sendCodeMail(String mail, String subject, Locale locale, String transactionId) {
        Email mailSendVar = new Email();
        mailSendVar.setFrom("name", mail);
        mailSendVar.setSendAt(new Date());
        mailSendVar.setTemplateId("0r83ql3k2om4zw1j");
        mailSendVar.setSubject("");
        mailSend.sendMail(mailSendVar, transactionId);
    }
}
