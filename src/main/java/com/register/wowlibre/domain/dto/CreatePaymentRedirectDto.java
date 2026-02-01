package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.math.*;

@Builder
public class CreatePaymentRedirectDto {
    public String redirect;
    public String confirmationUrl;
    public String responseUrl;
    public String buyerEmail;
    public String currency;
    public String taxReturnBase;
    public String tax;
    public BigDecimal amount;
    public String referenceCode;
    public String description;
    public boolean isPayment;
    public PaymentType paymentType;
    public PayUCredentialsModel payu;
}
