package com.register.wowlibre.domain.port.out.payu_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.*;

public interface SavePayuCredentials {
    void save(PayuCredentialsEntity payuCredentialsEntity, String transactionId);

    void delete(PayuCredentialsEntity payuCredentials, String transactionId);
}
