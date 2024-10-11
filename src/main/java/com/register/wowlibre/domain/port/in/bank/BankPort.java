package com.register.wowlibre.domain.port.in.bank;

import com.register.wowlibre.domain.dto.*;

import java.util.*;

public interface BankPort {
    void applyForLoan(Long userId, Long accountId, Long characterId, Long serverId, Long planId, String transactionId);

    List<ServerAvailableBankDto> serverAvailableLoan(String transactionId);
}
