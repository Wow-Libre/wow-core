package com.register.wowlibre.domain.dto.comunication;

import lombok.*;

@Builder
public class MailSenderVars<T> {
    public final String emailFrom;
    public final String subject;
    public final Integer idTemplate;
    public final T data;
}
