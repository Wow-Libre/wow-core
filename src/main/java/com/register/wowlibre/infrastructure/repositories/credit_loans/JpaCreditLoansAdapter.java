package com.register.wowlibre.infrastructure.repositories.credit_loans;

import com.register.wowlibre.domain.port.out.credit_loans.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Repository
public class JpaCreditLoansAdapter implements ObtainCreditLoans, SaveCreditLoans {
    private final CreditLoansRepository creditLoansRepository;

    public JpaCreditLoansAdapter(CreditLoansRepository creditLoansRepository) {
        this.creditLoansRepository = creditLoansRepository;
    }

    @Override
    public List<CreditLoansEntity> findByUserIdAndStatusIsTrue(Long userId) {
        return creditLoansRepository.findByUserId_idAndStatusIsTrue(userId);
    }

    @Override
    public Optional<CreditLoansEntity> findByReferenceSerial(String referenceSerial) {
        return creditLoansRepository.findByReferenceSerial(referenceSerial);
    }

    @Override
    public List<CreditLoansEntity> creditPendingSend(String transactionId) {
        return creditLoansRepository.findByStatusIsTrueAndSendIsFalse(PageRequest.of(0, 50))
                .stream().toList();
    }

    @Override
    public List<CreditLoansEntity> creditRequestPending(LocalDateTime localDateTime, String transactionId) {
        return creditLoansRepository.findActiveCreditLoans(localDateTime);
    }

    @Override
    public void save(CreditLoansEntity creditLoansEntity, String transactionId) {
        creditLoansRepository.save(creditLoansEntity);
    }
}
