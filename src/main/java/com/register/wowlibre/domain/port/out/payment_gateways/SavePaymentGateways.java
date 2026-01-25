package com.register.wowlibre.domain.port.out.payment_gateways;

import com.register.wowlibre.infrastructure.entities.transactions.PaymentGatewaysEntity;

public interface SavePaymentGateways {
    PaymentGatewaysEntity save(PaymentGatewaysEntity paymentGatewaysEntity);
}
