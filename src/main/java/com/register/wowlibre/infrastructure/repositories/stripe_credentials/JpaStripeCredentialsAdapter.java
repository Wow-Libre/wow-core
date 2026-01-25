package com.register.wowlibre.infrastructure.repositories.stripe_credentials;

import com.register.wowlibre.domain.port.out.stripe_credentials.ObtainStripeCredentials;
import com.register.wowlibre.domain.port.out.stripe_credentials.SaveStripeCredentials;
import com.register.wowlibre.infrastructure.entities.transactions.StripeCredentialsEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaStripeCredentialsAdapter implements ObtainStripeCredentials, SaveStripeCredentials {
    private final StripeCredentialsRepository stripeCredentialsRepository;

    public JpaStripeCredentialsAdapter(StripeCredentialsRepository stripeCredentialsRepository) {
        this.stripeCredentialsRepository = stripeCredentialsRepository;
    }

    @Override
    public Optional<StripeCredentialsEntity> findByGatewayId(Long gatewayId, String transactionId) {
        return stripeCredentialsRepository.findByGatewayId(gatewayId);
    }

    @Override
    public StripeCredentialsEntity save(StripeCredentialsEntity stripeCredentialsEntity) {
        return stripeCredentialsRepository.save(stripeCredentialsEntity);
    }
}
