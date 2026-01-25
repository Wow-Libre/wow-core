package com.register.wowlibre.domain.port.out.payu_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.PayuCredentialsEntity;

import java.util.Optional;

public interface ObtainPayuCredentials {
    Optional<PayuCredentialsEntity> findByGatewayId(Long gatewayId, String transactionId);
}
