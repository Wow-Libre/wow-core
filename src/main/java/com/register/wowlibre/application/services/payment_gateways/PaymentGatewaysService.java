package com.register.wowlibre.application.services.payment_gateways;

import com.register.wowlibre.domain.enums.PaymentType;
import com.register.wowlibre.domain.port.in.payment_gateways.PaymentGatewaysPort;
import com.register.wowlibre.domain.port.out.payment_gateways.ObtainPaymentGateways;
import com.register.wowlibre.domain.port.out.stripe_credentials.ObtainStripeCredentials;
import com.register.wowlibre.domain.port.out.payu_credentials.ObtainPayuCredentials;
import com.register.wowlibre.infrastructure.entities.transactions.PaymentGatewaysEntity;
import com.register.wowlibre.infrastructure.entities.transactions.StripeCredentialsEntity;
import com.register.wowlibre.infrastructure.entities.transactions.PayuCredentialsEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentGatewaysService implements PaymentGatewaysPort {
    private final ObtainPaymentGateways obtainPaymentGateways;
    private final ObtainStripeCredentials obtainStripeCredentials;
    private final ObtainPayuCredentials obtainPayuCredentials;

    public PaymentGatewaysService(ObtainPaymentGateways obtainPaymentGateways,
                                   ObtainStripeCredentials obtainStripeCredentials,
                                   ObtainPayuCredentials obtainPayuCredentials) {
        this.obtainPaymentGateways = obtainPaymentGateways;
        this.obtainStripeCredentials = obtainStripeCredentials;
        this.obtainPayuCredentials = obtainPayuCredentials;
    }

    @Override
    public Optional<PaymentGatewaysEntity> findByType(PaymentType type, String transactionId) {
        return obtainPaymentGateways.findByType(type, transactionId);
    }

    @Override
    public Optional<PaymentGatewaysEntity> findByTypeAndIsActiveTrue(PaymentType type, String transactionId) {
        return obtainPaymentGateways.findByTypeAndIsActiveTrue(type, transactionId);
    }

    @Override
    public Optional<StripeCredentialsEntity> findStripeCredentialsByGatewayId(Long gatewayId, String transactionId) {
        return obtainStripeCredentials.findByGatewayId(gatewayId, transactionId);
    }

    @Override
    public Optional<PayuCredentialsEntity> findPayuCredentialsByGatewayId(Long gatewayId, String transactionId) {
        return obtainPayuCredentials.findByGatewayId(gatewayId, transactionId);
    }
}
