package com.register.wowlibre.application.services.account_validation;

import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.port.in.account_validation.AccountValidationPort;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.domain.port.out.account_game.ObtainAccountGamePort;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountValidationService implements AccountValidationPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountValidationService.class);

    private final RealmPort realmPort;
    private final ObtainAccountGamePort obtainAccountGamePort;

    public AccountValidationService(RealmPort realmPort, ObtainAccountGamePort obtainAccountGamePort) {
        this.realmPort = realmPort;
        this.obtainAccountGamePort = obtainAccountGamePort;
    }

    @Override
    public AccountVerificationDto verifyAccount(Long userId, Long accountId, Long realmId, String transactionId) {
        Optional<RealmEntity> realm = realmPort.findById(realmId, transactionId);

        if (realm.isEmpty()) {
            LOGGER.error("[AccountValidationService] [verifyAccount] Realm not found - realmId: {}, userId: {}, accountId: {}, transactionId: {}",
                    realmId, userId, accountId, transactionId);
            throw new InternalException("The realm where your character is currently located is not available",
                    transactionId);
        }

        Optional<AccountGameEntity> accountGame =
                obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId,
                        realm.get().getId(), transactionId);

        if (accountGame.isEmpty()) {
            LOGGER.error("[AccountValidationService] [verifyAccount] Account game not found or inactive - userId: {}, accountId: {}, realmId: {}, transactionId: {}",
                    userId, accountId, realmId, transactionId);
            throw new InternalException("Currently your account is not found or is not available, please contact " +
                    "support", transactionId);
        }
        
        return new AccountVerificationDto(realm.get(), accountGame.get());
    }
}

