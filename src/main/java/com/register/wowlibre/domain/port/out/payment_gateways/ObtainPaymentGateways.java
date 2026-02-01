package com.register.wowlibre.domain.port.out.payment_gateways;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface ObtainPaymentGateways {
    Optional<PaymentGatewaysEntity> findByPaymentType(PaymentType paymentType, String transactionId);

    List<PaymentGatewaysEntity> findByIsActiveIsTrue(String transactionId);

    Optional<PaymentGatewaysEntity> findByPaymentTypeId(Long paymentTypeId, String transactionId);

}
