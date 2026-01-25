package com.register.wowlibre.infrastructure.repositories.transactions;

import com.register.wowlibre.infrastructure.entities.transactions.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByReferenceNumber(String referenceNumber);
    List<TransactionEntity> findByUserId(Long userId);
    List<TransactionEntity> findByUserIdAndStatus(Long userId, String status);
    List<TransactionEntity> findByStatus(String status);
}
