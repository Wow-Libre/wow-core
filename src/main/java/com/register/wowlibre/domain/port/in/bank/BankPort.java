package com.register.wowlibre.domain.port.in.bank;

import com.register.wowlibre.domain.dto.RealmAvailableBankDto;

import java.util.List;

public interface BankPort {
    void applyForLoan(Long userId, Long accountId, Long characterId, Long realmId, Long planId, String transactionId);

    List<RealmAvailableBankDto> getAvailableLoansByRealm(String transactionId);
}
