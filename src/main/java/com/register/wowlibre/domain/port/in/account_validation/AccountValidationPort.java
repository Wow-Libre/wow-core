package com.register.wowlibre.domain.port.in.account_validation;

import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;

public interface AccountValidationPort {
    
    /**
     * Verifies that an account game exists, is active, and belongs to the specified user and realm.
     * 
     * @param userId The user ID
     * @param accountId The account game ID
     * @param realmId The realm ID
     * @param transactionId The transaction ID for tracking
     * @return AccountVerificationDto containing the verified realm and account game entities
     * @throws com.register.wowlibre.domain.exception.InternalException if realm or account game is not found or inactive
     */
    AccountVerificationDto verifyAccount(Long userId, Long accountId, Long realmId, String transactionId);
}

