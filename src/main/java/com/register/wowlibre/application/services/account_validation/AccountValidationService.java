package com.register.wowlibre.application.services.account_validation;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.account_validation.*;
import com.register.wowlibre.domain.port.out.user_validation.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Service
public class AccountValidationService implements AccountValidationPort {

    private final RandomString randomString;
    private final RandomString otpRandom;
    private final ObtainUserValidation obtainUserValidation;
    private final SaveUserValidation saveUserValidation;

    public AccountValidationService(@Qualifier("random-code") RandomString randomString,
                                    @Qualifier("random-send-otp") RandomString otpRandom,
                                    ObtainUserValidation obtainUserValidation, SaveUserValidation saveUserValidation) {
        this.randomString = randomString;
        this.otpRandom = otpRandom;
        this.obtainUserValidation = obtainUserValidation;
        this.saveUserValidation = saveUserValidation;
    }

    @Override
    //@Cacheable(value = "emailCodeCache", key = "#email")
    public String retrieveEmailCode(String email, String transactionId) {
        return obtainUserValidation.findByEmail(email, transactionId)
                .map(UserValidationEntity::getCode).orElseThrow(() -> new InternalException("NotFound " +
                        "retrieveEmailCode", transactionId));
    }

    @Override
    // @CacheEvict(value = "emailCodeCache", key = "#email")
    public void clearEmailCode(String email, String transactionId) {
        UserValidationEntity userValidation = obtainUserValidation
                .findByEmail(email, transactionId)
                .orElseGet(UserValidationEntity::new);

        userValidation.setEmail(email);
        userValidation.setCode(randomString.nextString());
        saveUserValidation.save(userValidation, transactionId);
    }

    @Override
    //@CachePut(value = "emailCodeCache", key = "#email")
    public String generateCodeMail(String email, String transactionId) {
        final String code = String.format("%s_%s", email, randomString.nextString());

        UserValidationEntity userValidation = obtainUserValidation
                .findByEmail(email, transactionId)
                .orElseGet(UserValidationEntity::new);

        userValidation.setEmail(email);
        userValidation.setCode(code);
        saveUserValidation.save(userValidation, transactionId);

        return code;
    }


    @Override
    // @CachePut(value = "recoveryPassword", key = "#email")
    public String generateCodeRecoverAccount(String email, String transactionId) {
        String otp = otpRandom.nextString();

        UserValidationEntity userValidation = obtainUserValidation
                .findByEmail(email, transactionId)
                .orElseGet(UserValidationEntity::new);

        userValidation.setEmail(email);
        userValidation.setOtp(otp);

        saveUserValidation.save(userValidation, transactionId);


        return otp;
    }

    @Override
    //@Cacheable(value = "recoveryPassword", key = "#email")
    public String getCodeEmailRecoverPassword(String email, String transactionId) {
        return obtainUserValidation.findByEmail(email, transactionId)
                .map(UserValidationEntity::getOtp).orElseThrow(() -> new InternalException("NotFound " +
                        "getCodeEmailRecoverPassword", transactionId));
    }
}
