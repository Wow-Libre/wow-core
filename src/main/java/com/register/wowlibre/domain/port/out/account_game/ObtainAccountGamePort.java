package com.register.wowlibre.domain.port.out.account_game;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainAccountGamePort {
    List<AccountGameEntity> findByUserIdAndStatusIsTrue(Long userId, int page, int size, String transactionId);

    Optional<AccountGameEntity> findByUserIdAndAccountIdAndStatusIsTrue(Long userId, Long accountId,
                                                                        String transactionId);

    Long accounts(Long userId);
}
