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
        save(email, transactionId, randomString.nextString());
    }

    @Override
    //@CachePut(value = "emailCodeCache", key = "#email")
    public String generateCodeMail(String email, String transactionId) {
        final String code = String.format("%s_%s", email, randomString.nextString());
        save(email, transactionId, code);
        return code;
    }

    private void save(String email, String transactionId, String code) {
        UserValidationEntity userValidationEntity =
                obtainUserValidation.findByEmail(email, transactionId).orElseThrow(() -> new InternalException(
                        "Error ", transactionId));
        userValidationEntity.setCode(code);
        saveUserValidation.save(userValidationEntity, transactionId);
    }

    private void updateOtp(String email, String transactionId, String otp) {
        UserValidationEntity userValidationEntity =
                obtainUserValidation.findByEmail(email, transactionId).orElseThrow(() -> new InternalException(
                        "Error ", transactionId));
        userValidationEntity.setOtp(otp);
        saveUserValidation.save(userValidationEntity, transactionId);
    }

    @Override
    // @CachePut(value = "recoveryPassword", key = "#email")
    public String generateCodeRecoverAccount(String email, String transactionId) {
        String otp = otpRandom.nextString();
        updateOtp(email, transactionId, otp);
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
