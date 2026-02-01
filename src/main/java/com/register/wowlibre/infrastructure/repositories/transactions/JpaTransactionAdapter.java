package com.register.wowlibre.infrastructure.repositories.transactions;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.port.out.transactions.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaTransactionAdapter implements ObtainTransaction, SaveTransaction {
    private final TransactionRepository transactionRepository;

    public JpaTransactionAdapter(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<TransactionEntity> findByReferenceNumber(String referenceNumber, String transactionId) {
        return transactionRepository.findByReferenceNumber(referenceNumber);
    }

    @Override
    public List<TransactionEntity> findByUserId(Long userId, int page, int size, String transactionId) {
        return transactionRepository.findByUserIdOrderByCreationDateDesc(userId, PageRequest.of(page, size)).stream().toList();
    }

    @Override
    public Long findByUserId(Long userId, String transactionId) {
        return transactionRepository.countByUserId(userId);
    }

    @Override
    public List<TransactionEntity> findByUserIdAndStatus(Long userId, String status, String transactionId) {
        return transactionRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<TransactionEntity> findByStatus(String status, String transactionId) {
        return transactionRepository.findByStatus(status);
    }

    @Override
    public Optional<TransactionEntity> findById(Long id, String transactionId) {
        return transactionRepository.findById(id);
    }

    @Override
    public Optional<TransactionEntity> findByReferenceNumberAndUserId(String reference, Long userId,
                                                                      String transactionId) {
        return transactionRepository.findByReferenceNumberAndUserId(reference, userId);
    }

    @Override
    public List<TransactionEntity> findByStatusIsPaidAndSendIsFalse(String transactionId) {
        return transactionRepository.findByStatusAndSendIsFalse(TransactionStatus.PAID.getType());
    }

    @Override
    public TransactionEntity save(TransactionEntity transactionEntity) {
        return transactionRepository.save(transactionEntity);
    }
}
