package com.register.wowlibre.domain.port.in.user;

import com.register.wowlibre.domain.dto.UserDto;
import com.register.wowlibre.domain.model.UserModel;
import com.register.wowlibre.domain.security.JwtDto;

public interface UserPort {
    JwtDto create(UserDto accountWebDto, String transactionId);

    UserModel findByEmail(String email, String transactionId);

    UserModel findByPhone(String cellPhone, String transactionId);

    UserModel findByUserId(Long userId, String transactionId);

}
