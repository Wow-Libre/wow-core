package com.register.wowlibre.domain.port.out.stripe_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.StripeCredentialsEntity;

public interface SaveStripeCredentials {
    StripeCredentialsEntity save(StripeCredentialsEntity stripeCredentialsEntity);
}
