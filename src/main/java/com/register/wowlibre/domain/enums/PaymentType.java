package com.register.wowlibre.domain.enums;

import lombok.*;

import java.util.*;

@Getter
@AllArgsConstructor
public enum PaymentType {
    PAYU("PAYU"),
    STRIPE("STRIPE"),
    NOT_MAPPED("NOT_MAPPED");

    private final String type;

    public static PaymentType getType(String type) {
        return Arrays.stream(values())
                .filter(transaction -> transaction.type.equalsIgnoreCase(type))
                .findFirst()
                .orElse(NOT_MAPPED);
    }
}
