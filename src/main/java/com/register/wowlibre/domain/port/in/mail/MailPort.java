package com.register.wowlibre.domain.port.in.mail;

import java.util.*;

public interface MailPort {
    void sendCodeMail(String mail, String subject, String code, Locale locale, String transactionId);

}
