package com.register.wowlibre.infrastructure.repositories.account_game;

import com.register.wowlibre.domain.port.out.account_game.ObtainAccountGamePort;
import com.register.wowlibre.domain.port.out.account_game.SaveAccountGamePort;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAccountGameAdapter implements ObtainAccountGamePort, SaveAccountGamePort {
    private final AccountGameRepository accountGameRepository;

    public JpaAccountGameAdapter(AccountGameRepository accountGameRepository) {
        this.accountGameRepository = accountGameRepository;
    }

    @Override
    public List<AccountGameEntity> findByUserIdAndStatusIsTrue(Long userId, int page, int size, String transactionId) {
        return accountGameRepository.findByUserId_IdAndStatusIsTrue(userId, PageRequest.of(page, size)).stream().toList();
    }

    @Override
    public Optional<AccountGameEntity> findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(Long userId,
                                                                                         Long accountId,
                                                                                         Long realmId,
                                                                                         String transactionId) {
        return accountGameRepository.findByUserId_IdAndAccountIdAndRealmId_idAndStatusIsTrue(userId, accountId,
                realmId);
    }

    @Override
    public Long accounts(Long userId) {
        return accountGameRepository.countByUserId(userId);
    }

    @Override
    public List<AccountGameEntity> findByUserIdAndRealmNameAndUsernameStatusIsTrue(Long userId, int page, int size,
                                                                                   String realmName, String username,
                                                                                   String transactionId) {
        return accountGameRepository.findByUserId_IdAndStatusIsTrueAndRealmNameAndUsername(
                realmName, userId, username, PageRequest.of(page, size)).stream().toList();
    }

    @Override
    public List<AccountGameEntity> findByUserIdAndRealmId(Long userId, Long serverId, String transactionId) {
        return accountGameRepository.findByUserId_IdAndRealmId_IdAndStatusIsTrue(userId, serverId);
    }

    @Override
    public Optional<AccountGameEntity> findByIdAndUserId(Long id, Long userId, String transactionId) {
        return accountGameRepository.findByIdAndUserId_Id(id, userId);
    }

    @Override
    public AccountGameEntity save(AccountGameEntity accountGameEntity, String transactionId) {
        return accountGameRepository.save(accountGameEntity);
    }

    @Override
    public long countActiveAccountsByUserId(Long userId, String transactionId) {
        return accountGameRepository.countActiveAccountsByUserId(userId);
    }

    @Override
    public long countDistinctRealmsByUserId(Long userId, String transactionId) {
        return accountGameRepository.countDistinctRealmsByUserId(userId);
    }
}
