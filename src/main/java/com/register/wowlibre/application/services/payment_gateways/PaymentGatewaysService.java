package com.register.wowlibre.application.services.payment_gateways;

import com.register.wowlibre.application.services.payment_method.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.payment_gateways.*;
import com.register.wowlibre.domain.port.out.payment_gateways.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static com.register.wowlibre.domain.enums.PaymentType.*;

@Service
public class PaymentGatewaysService implements PaymentGatewaysPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGatewayService.class);

    private final ObtainPaymentGateways obtainPaymentGateway;
    private final SavePaymentGateways savePaymentGateway;
    private final PaymentMethodFactory paymentMethodFactory;

    public PaymentGatewaysService(ObtainPaymentGateways obtainPaymentGateway, SavePaymentGateways savePaymentGateway,
                                  PaymentMethodFactory paymentMethodFactory) {
        this.obtainPaymentGateway = obtainPaymentGateway;
        this.savePaymentGateway = savePaymentGateway;
        this.paymentMethodFactory = paymentMethodFactory;
    }


    @Override
    public PaymentGatewayModel generateUrlPayment(PaymentType paymentType, String currency, BigDecimal amount,
                                                  Integer quantity, String productName, String referenceCode,
                                                  String transactionId) {

        Optional<PaymentGatewaysEntity> paymentAvailable = obtainPaymentGateway.findByPaymentType(paymentType,
                transactionId);

        if (paymentAvailable.isEmpty()) {
            LOGGER.error("Payment method {} not available", paymentType);
            throw new InternalException("Method payment is disable", transactionId);
        }

        PaymentGatewaysEntity paymentGateway = paymentAvailable.get();

        PaymentMethod paymentMethod = paymentMethodFactory.createPaymentMethod(paymentType, transactionId);

        return paymentMethod.payment(paymentGateway.getId(), currency, amount, quantity, productName, referenceCode,
                transactionId);
    }

    @Override
    @Transactional
    public void createPayment(String paymentType, String name, Map<String, String> credentials, String transactionId) {

        PaymentType paymentMethodType = PaymentType.getType(paymentType);

        if (paymentMethodType == null || paymentMethodType.equals(NOT_MAPPED)) {
            throw new InternalException("Payment Method Type Invalid", transactionId);
        }

        Optional<PaymentGatewaysEntity> paymentAvailable = obtainPaymentGateway.findByPaymentType(paymentMethodType,
                transactionId);

        if (paymentAvailable.isPresent()) {
            LOGGER.error("Payment method {} already exist", paymentType);
            throw new InternalException("Payment Method Exist", transactionId);
        }

        PaymentGatewaysEntity paymentMethod = new PaymentGatewaysEntity();
        paymentMethod.setName(name);
        paymentMethod.setActive(true);
        paymentMethod.setCreatedAt(LocalDateTime.now());
        paymentMethod.setType(paymentMethodType);
        savePaymentGateway.save(paymentMethod, transactionId);
        PaymentMethod paymentMethodInstance = paymentMethodFactory.createPaymentMethod(paymentMethodType,
                transactionId);
        paymentMethodInstance.vinculate(paymentMethod, credentials, transactionId);
    }

    @Override
    public List<PaymentMethodsDto> paymentMethods(String transactionId) {

        List<PaymentGatewaysEntity> paymentMethodGateways = obtainPaymentGateway.findByIsActiveIsTrue(transactionId);

        return paymentMethodGateways.stream()
                .map(payment -> new PaymentMethodsDto(payment.getId(), payment.getType(),
                        payment.getName(), payment.getCreatedAt()))
                .toList();
    }

    @Override
    public void deletePayment(Long paymentTypeId, String transactionId) {

        Optional<PaymentGatewaysEntity> paymentGatewayDelete = obtainPaymentGateway.findByPaymentTypeId(paymentTypeId,
                transactionId);

        if (paymentGatewayDelete.isEmpty()) {
            LOGGER.error("Payment method id {} not exist", paymentTypeId);
            throw new InternalException("Payment Method Not Exist", transactionId);
        }

        PaymentMethod paymentMethodInstance = paymentMethodFactory
                .createPaymentMethod(paymentGatewayDelete.get().getType(), transactionId);
        paymentMethodInstance.delete(paymentGatewayDelete.get(), transactionId);
        savePaymentGateway.delete(paymentGatewayDelete.get(), transactionId);
    }

    @Override
    public PaymentStatus paymentStatus(PaymentTransaction paymentTransaction, PaymentType paymentType,
                                       String transactionId) {
        Optional<PaymentGatewaysEntity> paymentAvailable = obtainPaymentGateway.findByPaymentType(paymentType,
                transactionId);

        if (paymentAvailable.isEmpty()) {
            LOGGER.error("[isValidPaymentSignature] Payment method {} not available", paymentType);
            throw new InternalException("Method payment is disable", transactionId);
        }

        PaymentMethod paymentMethodInstance = paymentMethodFactory.createPaymentMethod(paymentType, transactionId);

        if (!paymentMethodInstance.validateCredentials(paymentAvailable.get(), paymentTransaction, transactionId)) {
            throw new InternalException("Invalid payment signature", transactionId);
        }

        return paymentMethodInstance.paymentStatus(paymentTransaction, transactionId);
    }

    @Override
    public PaymentStatus findByStatus(PaymentType paymentType, String referenceCode, String id, String transactionId) {
        Optional<PaymentGatewaysEntity> paymentAvailable = obtainPaymentGateway.findByPaymentType(paymentType,
                transactionId);

        if (paymentAvailable.isEmpty()) {
            LOGGER.error("[credentials] Payment method {} not available", paymentType);
            throw new InternalException("Method payment is disable", transactionId);
        }

        PaymentMethod paymentMethodInstance = paymentMethodFactory.createPaymentMethod(paymentType, transactionId);

        return paymentMethodInstance.findByStatus(paymentAvailable.get(), referenceCode, id, transactionId);
    }
}
