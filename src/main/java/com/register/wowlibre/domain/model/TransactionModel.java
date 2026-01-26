package com.register.wowlibre.domain.model;

import com.register.wowlibre.domain.enums.*;
import lombok.*;

@Getter
@Builder
public class TransactionModel {
    private Long userId;
    private Long accountId;
    private Long realmId;
    private boolean isSubscription;
    private String productReference;
    private PaymentType paymentType;
}
