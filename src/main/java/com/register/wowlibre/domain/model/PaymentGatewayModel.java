package com.register.wowlibre.domain.model;

import com.register.wowlibre.domain.enums.*;
import lombok.*;

@Builder
public class PaymentGatewayModel {
    public final String id;
    public final String payment;
    public final PaymentType type;
    public final String redirect;
    public final String successUrl;
    public final String cancelUrl;
    public final String webhookUrl;
    public final String signature;
    public final PayU payu;

    @Builder
    public record PayU(String merchantId, String accountId) {
    }
}
