package com.register.wowlibre.domain.port.in.account_validation;

public interface AccountValidationPort {
    String retrieveEmailCode(String email);

    String generateCodeMail(String mail);
}
