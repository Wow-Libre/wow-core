package com.register.wowlibre.domain.port.out.payu_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.PayuCredentialsEntity;

public interface SavePayuCredentials {
    PayuCredentialsEntity save(PayuCredentialsEntity payuCredentialsEntity);
}
