package com.register.wowlibre.infrastructure.repositories.payment_gateways;

import com.register.wowlibre.domain.enums.PaymentType;
import com.register.wowlibre.infrastructure.entities.transactions.PaymentGatewaysEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentGatewaysRepository extends CrudRepository<PaymentGatewaysEntity, Long> {
    Optional<PaymentGatewaysEntity> findByType(PaymentType type);
    Optional<PaymentGatewaysEntity> findByTypeAndIsActiveTrue(PaymentType type);
    List<PaymentGatewaysEntity> findByIsActiveTrue();
}
