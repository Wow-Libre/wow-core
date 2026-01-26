package com.register.wowlibre.domain.port.out.stripe_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.*;

public interface SaveStripeCredentials {
    void save(StripeCredentialsEntity stripeCredentials, String transactionId);

    void delete(StripeCredentialsEntity stripeCredentials, String transactionId);
}
