package com.register.wowlibre.domain.port.in.account_validation;

public interface AccountValidationPort {
    String retrieveEmailCode(String email, String transactionId);

     void clearEmailCode(String email, String transactionId);

    String generateCodeMail(String email, String transactionId);
    String generateCodeRecoverAccount(String mail, String transactionId);

    String getCodeEmailRecoverPassword(String email, String transactionId);
}
