package com.register.wowlibre.domain.model;


import com.register.wowlibre.infrastructure.entities.transactions.*;

public record PaymentApplicableModel(boolean isPayment, Double amount, String currency, String orderId,
                                     String description, String tax,
                                     String returnTax, String productName, TransactionEntity transactionEntity) {
}
