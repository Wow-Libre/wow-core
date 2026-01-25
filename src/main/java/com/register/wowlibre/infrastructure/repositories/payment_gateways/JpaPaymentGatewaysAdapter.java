package com.register.wowlibre.infrastructure.repositories.payment_gateways;

import com.register.wowlibre.domain.enums.PaymentType;
import com.register.wowlibre.domain.port.out.payment_gateways.ObtainPaymentGateways;
import com.register.wowlibre.domain.port.out.payment_gateways.SavePaymentGateways;
import com.register.wowlibre.infrastructure.entities.transactions.PaymentGatewaysEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaPaymentGatewaysAdapter implements ObtainPaymentGateways, SavePaymentGateways {
    private final PaymentGatewaysRepository paymentGatewaysRepository;

    public JpaPaymentGatewaysAdapter(PaymentGatewaysRepository paymentGatewaysRepository) {
        this.paymentGatewaysRepository = paymentGatewaysRepository;
    }

    @Override
    public Optional<PaymentGatewaysEntity> findByType(PaymentType type, String transactionId) {
        return paymentGatewaysRepository.findByType(type);
    }

    @Override
    public Optional<PaymentGatewaysEntity> findByTypeAndIsActiveTrue(PaymentType type, String transactionId) {
        return paymentGatewaysRepository.findByTypeAndIsActiveTrue(type);
    }

    @Override
    public List<PaymentGatewaysEntity> findByIsActiveTrue(String transactionId) {
        return paymentGatewaysRepository.findByIsActiveTrue();
    }

    @Override
    public Optional<PaymentGatewaysEntity> findById(Long id, String transactionId) {
        return paymentGatewaysRepository.findById(id);
    }

    @Override
    public PaymentGatewaysEntity save(PaymentGatewaysEntity paymentGatewaysEntity) {
        return paymentGatewaysRepository.save(paymentGatewaysEntity);
    }
}
