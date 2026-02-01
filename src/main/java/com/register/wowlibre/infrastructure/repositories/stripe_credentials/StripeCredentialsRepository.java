package com.register.wowlibre.infrastructure.repositories.stripe_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.StripeCredentialsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StripeCredentialsRepository extends CrudRepository<StripeCredentialsEntity, Long> {
    Optional<StripeCredentialsEntity> findByGatewayId(Long gatewayId);
}
