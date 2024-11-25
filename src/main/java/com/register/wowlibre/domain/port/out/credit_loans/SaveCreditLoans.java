package com.register.wowlibre.domain.port.out.credit_loans;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveCreditLoans {
    void save(CreditLoansEntity creditLoansEntity, String transactionId);
}
