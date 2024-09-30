package com.register.wowlibre.domain.port.out.account_game;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainAccountGamePort {
    List<AccountGameEntity> findByUserIdAndStatusIsTrue(Long userId, int size, int page, String transactionId);
}
