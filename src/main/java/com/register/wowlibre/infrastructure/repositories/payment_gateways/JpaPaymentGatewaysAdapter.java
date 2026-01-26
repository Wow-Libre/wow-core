package com.register.wowlibre.infrastructure.repositories.payment_gateways;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.port.out.payment_gateways.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaPaymentGatewaysAdapter implements ObtainPaymentGateways, SavePaymentGateways {
    private final PaymentGatewaysRepository paymentGatewaysRepository;

    public JpaPaymentGatewaysAdapter(PaymentGatewaysRepository paymentGatewaysRepository) {
        this.paymentGatewaysRepository = paymentGatewaysRepository;
    }

    @Override
    public Optional<PaymentGatewaysEntity> findByPaymentType(PaymentType paymentType, String transactionId) {
        return paymentGatewaysRepository.findByTypeAndIsActiveIsTrue(paymentType);
    }

    @Override
    public List<PaymentGatewaysEntity> findByIsActiveIsTrue(String transactionId) {
        return paymentGatewaysRepository.findByIsActiveIsTrue();
    }

    @Override
    public Optional<PaymentGatewaysEntity> findByPaymentTypeId(Long paymentTypeId, String transactionId) {
        return paymentGatewaysRepository.findById(paymentTypeId);
    }


    @Override
    public void save(PaymentGatewaysEntity paymentGateways, String transactionId) {
        paymentGatewaysRepository.save(paymentGateways);
    }

    @Override
    public void delete(PaymentGatewaysEntity paymentGateways, String transactionId) {
        paymentGatewaysRepository.delete(paymentGateways);

    }
}
