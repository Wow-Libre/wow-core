package com.register.wowlibre.domain.port.out.payment_gateways;

import com.register.wowlibre.infrastructure.entities.transactions.*;

public interface SavePaymentGateways {
    void save(PaymentGatewaysEntity paymentGateways, String transactionId);

    void delete(PaymentGatewaysEntity paymentGateways, String transactionId);
}
