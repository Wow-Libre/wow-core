package com.register.wowlibre.infrastructure.repositories.transactions;

import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByReferenceNumber(String referenceNumber);

    List<TransactionEntity> findByUserId(Long userId);

    List<TransactionEntity> findByUserIdAndStatus(Long userId, String status);

    List<TransactionEntity> findByStatus(String status);

    Page<TransactionEntity> findByUserIdOrderByCreationDateDesc(Long userId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM TransactionEntity a WHERE a.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    Optional<TransactionEntity> findByReferenceNumberAndUserId(String referenceNumber, Long userId);

    @Query("SELECT a FROM TransactionEntity a WHERE (a.status = :status) AND a.send = " +
            "false")
    List<TransactionEntity> findByStatusAndSendIsFalse(@Param("status") String status);
}
