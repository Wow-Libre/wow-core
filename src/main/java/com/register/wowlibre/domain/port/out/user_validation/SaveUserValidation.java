package com.register.wowlibre.domain.port.out.user_validation;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveUserValidation {
    void save(UserValidationEntity userValidationEntity, String transactionId);
}
