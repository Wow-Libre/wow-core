package com.register.wowlibre.domain.port.in.account_game;

public interface AccountGamePort {

    void create(Long userId, String serverName, String expansion, String username, String password,
                boolean rebuildUsername, String transactionId);
}
