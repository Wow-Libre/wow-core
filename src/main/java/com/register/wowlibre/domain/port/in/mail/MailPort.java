package com.register.wowlibre.domain.port.in.mail;

import java.util.*;

public interface MailPort {
    void sendCodeMail(String mail, String subject, Locale locale, String transactionId);

}
