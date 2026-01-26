package com.register.wowlibre.domain.port.out.stripe_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface ObtainStripeCredentials {
    Optional<StripeCredentialsEntity> findByPayUCredentials(Long gatewayId, String transactionId);
}
