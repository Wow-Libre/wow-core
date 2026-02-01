package com.register.wowlibre.domain.port.out.transactions;

import com.register.wowlibre.infrastructure.entities.transactions.TransactionEntity;

public interface SaveTransaction {
    TransactionEntity save(TransactionEntity transactionEntity);
}
