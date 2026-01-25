package com.register.wowlibre.domain.port.out.transactions;

import com.register.wowlibre.infrastructure.entities.transactions.TransactionEntity;

import java.util.List;
import java.util.Optional;

public interface ObtainTransaction {
    Optional<TransactionEntity> findByReferenceNumber(String referenceNumber, String transactionId);
    List<TransactionEntity> findByUserId(Long userId, String transactionId);
    List<TransactionEntity> findByUserIdAndStatus(Long userId, String status, String transactionId);
    List<TransactionEntity> findByStatus(String status, String transactionId);
    Optional<TransactionEntity> findById(Long id, String transactionId);
}
