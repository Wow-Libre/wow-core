package com.register.wowlibre.infrastructure.repositories.credit_loans;

import com.register.wowlibre.domain.port.out.credit_loans.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

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
    public void save(CreditLoansEntity creditLoansEntity, String transactionId) {
        creditLoansRepository.save(creditLoansEntity);
    }
}
