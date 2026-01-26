package com.register.wowlibre.domain.port.in.transaction;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface TransactionPort {
    void purchase(Long serverId, Long userId, Long accountId, String reference, List<ItemQuantityModel> items,
                  Double amount, String transactionId);

    void sendSubscriptionBenefits(Long serverId, Long userId, Long accountId, Long characterId,
                                  List<ItemQuantityModel> items, String benefitType, Double amount,
                                  String transactionId);

    TransactionsDto transactionsByUserId(Long userId, Integer page, Integer size, String transactionId);

    void save(TransactionEntity transaction, String transactionId);

    Optional<TransactionEntity> findByReferenceNumber(String referenceNumber, String transactionId);

    Optional<TransactionEntity> findByReferenceNumberAndUserId(String referenceNumber, Long userId,
                                                               String transactionId);

    PaymentApplicableModel isRealPaymentApplicable(TransactionModel transaction, String transactionId);

}
