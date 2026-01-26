package com.register.wowlibre.application.services.payment_method;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.math.*;
import java.util.*;

public abstract class PaymentMethod {
    public abstract PaymentGatewayModel payment(Long idMethodGateway, String currency, BigDecimal amount,
                                                Integer quantity, String productName,
                                                String referenceCode, String transactionId);

    public abstract void vinculate(PaymentGatewaysEntity paymentMethod,
                                   Map<String, String> credentials, String transactionId);

    public abstract void delete(PaymentGatewaysEntity paymentMethod, String transactionId);

    public abstract boolean validateCredentials(PaymentGatewaysEntity paymentGateway,
                                                PaymentTransaction paymentTransaction, String transactionId);

    public abstract PaymentStatus paymentStatus(PaymentTransaction paymentTransaction, String transactionId);

    public abstract PaymentStatus findByStatus(PaymentGatewaysEntity paymentGateway, String referenceCode, String id,
                                               String transactionId);
}
