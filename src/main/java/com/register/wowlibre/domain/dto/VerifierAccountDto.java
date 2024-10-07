package com.register.wowlibre.domain.dto;

import com.register.wowlibre.infrastructure.entities.*;

public class VerifierAccountDto {
    public final ServerEntity server;
    public final AccountGameEntity accountGame;

    public VerifierAccountDto(ServerEntity server, AccountGameEntity accountGame) {
        this.server = server;
        this.accountGame = accountGame;
    }
}
