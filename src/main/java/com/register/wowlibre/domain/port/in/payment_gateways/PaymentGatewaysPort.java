package com.register.wowlibre.domain.port.in.payment_gateways;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.model.*;

import java.math.*;
import java.util.*;

public interface PaymentGatewaysPort {
    PaymentGatewayModel generateUrlPayment(PaymentType paymentType, String currency, BigDecimal amount,
                                           Integer quantity, String productName, String referenceCode,
                                           String transactionId);

    void createPayment(String paymentType, String name, Map<String, String> credentials, String transactionId);

    List<PaymentMethodsDto> paymentMethods(String transactionId);

    void deletePayment(Long paymentTypeId, String transactionId);

    PaymentStatus paymentStatus(PaymentTransaction paymentTransaction, PaymentType paymentType,
                                String transactionId);

    PaymentStatus findByStatus(PaymentType paymentType, String referenceCode, String id, String transactionId);
}
