package com.register.wowlibre.domain.port.in.payment_gateways;

import com.register.wowlibre.domain.enums.PaymentType;
import com.register.wowlibre.infrastructure.entities.transactions.PaymentGatewaysEntity;
import com.register.wowlibre.infrastructure.entities.transactions.StripeCredentialsEntity;
import com.register.wowlibre.infrastructure.entities.transactions.PayuCredentialsEntity;

import java.util.Optional;

public interface PaymentGatewaysPort {
    Optional<PaymentGatewaysEntity> findByType(PaymentType type, String transactionId);
    Optional<PaymentGatewaysEntity> findByTypeAndIsActiveTrue(PaymentType type, String transactionId);
    Optional<StripeCredentialsEntity> findStripeCredentialsByGatewayId(Long gatewayId, String transactionId);
    Optional<PayuCredentialsEntity> findPayuCredentialsByGatewayId(Long gatewayId, String transactionId);
}
