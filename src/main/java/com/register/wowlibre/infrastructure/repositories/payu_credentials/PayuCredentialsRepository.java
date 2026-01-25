package com.register.wowlibre.infrastructure.repositories.payu_credentials;

import com.register.wowlibre.infrastructure.entities.transactions.PayuCredentialsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PayuCredentialsRepository extends CrudRepository<PayuCredentialsEntity, Long> {
    Optional<PayuCredentialsEntity> findByGatewayId(Long gatewayId);
}
