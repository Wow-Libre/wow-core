package com.register.wowlibre.domain.port.out.credit_loans;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainCreditLoans {
    List<CreditLoansEntity> findByUserIdAndStatusIsTrue(Long userId);

    Optional<CreditLoansEntity> findByReferenceSerial(String referenceSerial);
}
