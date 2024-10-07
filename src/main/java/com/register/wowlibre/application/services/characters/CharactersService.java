package com.register.wowlibre.application.services.characters;

import com.register.wowlibre.application.services.integrator.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.characters.*;
import com.register.wowlibre.domain.port.in.server.*;
import org.springframework.stereotype.*;

@Service
public class CharactersService implements CharactersPort {
    private final IntegratorService integratorService;
    private final AccountGamePort accountGamePort;

    public CharactersService(IntegratorService integratorService, ServerPort serverPort,
                             AccountGamePort accountGamePort) {
        this.integratorService = integratorService;
        this.accountGamePort = accountGamePort;
    }


    @Override
    public CharactersDto characters(Long userId, Long accountId, Long serverId, String transactionId) {

        VerifierAccountDto verifierAccountDto = accountGamePort.verify(userId, accountId, serverId, transactionId);

        return integratorService.characters(ServerMapper.toModel(verifierAccountDto.server),
                accountId, userId, transactionId);
    }
}
