package com.register.wowlibre.domain.dto;

import lombok.*;

@Builder
@Data
public class PaymentTransaction {
    private String date;
    private int paymentMethodType;
    private String sign;
    private String paymentMethod;
    private String currency;
    private String value;
    private String paymentMethodName;
    private String emailBuyer;
    private String accountId;
    private String referenceSale;
    private String merchantId;
    private String statePol;
    private String responseMessagePol;
    private StripePayment stripePayment;

    @AllArgsConstructor
    @Data
    public static class StripePayment {
        private String payloadStripe;
        private String status;
        private Boolean paid;
        private Boolean captured;
    }
}
