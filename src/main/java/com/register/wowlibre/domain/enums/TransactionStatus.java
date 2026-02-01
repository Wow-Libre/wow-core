package com.register.wowlibre.domain.enums;

import lombok.*;

import java.util.*;

@Getter
@AllArgsConstructor
public enum TransactionStatus {
    PAID("PAID", 100),
    CREATED("CREATED", 1),
    DELIVERED("DELIVERED", 100),
    REJECTED("REJECTED", 100),
    ERROR("ERROR", 100),
    INSUFFICIENT_MONEY("INSUFFICIENT_MONEY", 0),
    PENDING("PENDING", 50);

    private final String type;
    private final int status;

    public static TransactionStatus getType(String type) {
        return Arrays.stream(values())
                .filter(transaction -> transaction.type.equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
