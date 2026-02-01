package com.register.wowlibre.domain.port.in.payment;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;

public interface PaymentPort {

    void processPayment(PaymentTransaction paymentTransaction, PaymentType paymentType, String transactionId);

    CreatePaymentRedirectDto createPayment(Long userId, String email, CreatePaymentDto createPaymentDto,
                                           String transactionId);

}
