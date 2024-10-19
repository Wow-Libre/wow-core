package com.register.wowlibre.application.services.account_validation;

import com.register.wowlibre.domain.port.in.account_validation.*;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;

@Service
public class AccountValidationService implements AccountValidationPort {

    private final RandomString randomString;

    public AccountValidationService(@Qualifier("random-code") RandomString randomString) {
        this.randomString = randomString;
    }

    @Override
    @Cacheable(value = "emailCodeCache", key = "#email")
    public String retrieveEmailCode(String email) {
        // Este método se utiliza para obtener el código de la caché
        return null;
    }

    @Override
    @CachePut(value = "emailCodeCache", key = "#email")
    public String generateCodeMail(String email) {
        return String.format("%s_%s", email, randomString.nextString());
    }
}
