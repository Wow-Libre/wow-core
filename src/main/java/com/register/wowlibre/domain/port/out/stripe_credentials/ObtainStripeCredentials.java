package com.register.wowlibre.domain.port.out.stripe_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.StripeCredentialsEntity;

import java.util.Optional;

public interface ObtainStripeCredentials {
    Optional<StripeCredentialsEntity> findByGatewayId(Long gatewayId, String transactionId);
}
