package com.register.wowlibre.domain.port.out.payu_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface ObtainPayuCredentials {
    Optional<PayuCredentialsEntity> findByPayUCredentials(Long gatewayId, String transactionId);
}
