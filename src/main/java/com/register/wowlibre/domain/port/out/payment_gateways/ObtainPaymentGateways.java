package com.register.wowlibre.domain.port.out.payment_gateways;

import com.register.wowlibre.domain.enums.PaymentType;
import com.register.wowlibre.infrastructure.entities.transactions.PaymentGatewaysEntity;

import java.util.List;
import java.util.Optional;

public interface ObtainPaymentGateways {
    Optional<PaymentGatewaysEntity> findByType(PaymentType type, String transactionId);
    Optional<PaymentGatewaysEntity> findByTypeAndIsActiveTrue(PaymentType type, String transactionId);
    List<PaymentGatewaysEntity> findByIsActiveTrue(String transactionId);
    Optional<PaymentGatewaysEntity> findById(Long id, String transactionId);
}
