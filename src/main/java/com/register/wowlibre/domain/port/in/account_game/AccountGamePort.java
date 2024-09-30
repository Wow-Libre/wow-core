package com.register.wowlibre.domain.port.in.account_game;

import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface AccountGamePort {
    List<AccountGameModel> accounts(Long userId, String transactionId);

    void create(Long userId, String serverName, String expansion, Long accountId, String transactionId);
}
