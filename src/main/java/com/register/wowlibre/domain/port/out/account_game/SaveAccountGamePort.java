package com.register.wowlibre.domain.port.out.account_game;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveAccountGamePort {
    AccountGameEntity save(AccountGameEntity accountGameEntity, String transactionId);
}
