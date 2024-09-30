package com.register.wowlibre.infrastructure.repositories.account_game;

import com.register.wowlibre.domain.port.out.account_game.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaAccountGameAdapter implements ObtainAccountGamePort, SaveAccountGamePort {
    private final AccountGameRepository accountGameRepository;

    public JpaAccountGameAdapter(AccountGameRepository accountGameRepository) {
        this.accountGameRepository = accountGameRepository;
    }

    @Override
    public List<AccountGameEntity> findByUserIdAndStatusIsTrue(Long userId, int size, int page, String transactionId) {
        return accountGameRepository.findByUserId_IdAndStatusIsTrue(userId, PageRequest.of(page, size)).stream().toList();
    }

    @Override
    public AccountGameEntity save(AccountGameEntity accountGameEntity, String transactionId) {
        return accountGameRepository.save(accountGameEntity);
    }
}
