package com.register.wowlibre.domain.port.out.transactions;

import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface ObtainTransaction {
    Optional<TransactionEntity> findByReferenceNumber(String referenceNumber, String transactionId);

    List<TransactionEntity> findByUserId(Long userId, int page, int size, String transactionId);

    Long findByUserId(Long userId, String transactionId);

    List<TransactionEntity> findByUserIdAndStatus(Long userId, String status, String transactionId);

    List<TransactionEntity> findByStatus(String status, String transactionId);

    Optional<TransactionEntity> findById(Long id, String transactionId);

    Optional<TransactionEntity> findByReferenceNumberAndUserId(String reference, Long userId, String transactionId);

    List<TransactionEntity> findByStatusIsPaidAndSendIsFalse(String transactionId);

}
