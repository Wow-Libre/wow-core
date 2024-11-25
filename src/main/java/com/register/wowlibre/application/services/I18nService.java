package com.register.wowlibre.application.services;

import org.springframework.context.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class I18nService {

    private final MessageSource messageSource;

    public I18nService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String tr(String key, Object[] objects, Locale locale) {
        return messageSource.getMessage(key, objects, locale);
    }

    public String tr(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
