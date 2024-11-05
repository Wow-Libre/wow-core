package com.register.wowlibre.domain.port.in.user;

import com.register.wowlibre.domain.dto.UserDto;
import com.register.wowlibre.domain.model.UserModel;
import com.register.wowlibre.domain.security.JwtDto;
import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface UserPort {
    JwtDto create(UserDto accountWebDto, Locale locale, String transactionId);

    UserModel findByEmail(String email, String transactionId);

    UserModel findByPhone(String cellPhone, String transactionId);

    Optional<UserEntity> findByUserId(Long userId, String transactionId);

    Optional<UserEntity> findByEmailEntity(String email, String transactionId);

    void validationAccount(Long userId, String code, String transactionId);

    void resetPassword(String email, String transactionId);

    void validateOtpRecoverPassword(String email, String code, String transactionId);
}
