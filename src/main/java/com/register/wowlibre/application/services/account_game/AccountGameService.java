package com.register.wowlibre.application.services.account_game;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.out.account_game.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class AccountGameService implements AccountGamePort {
    private final SaveAccountGamePort saveAccountGamePort;
    private final ServerPort serverPort;
    private final UserPort userPort;
    private final IntegratorPort integratorPort;

    public AccountGameService(SaveAccountGamePort saveAccountGamePort, ServerPort serverPort, UserPort userPort,
                              IntegratorPort integratorPort) {
        this.saveAccountGamePort = saveAccountGamePort;
        this.serverPort = serverPort;
        this.userPort = userPort;
        this.integratorPort = integratorPort;
    }

    @Override
    public void create(Long userId, String serverName, String expansion, String username, String password,
                       boolean rebuildUsername, String transactionId) {

        Optional<UserEntity> userModel = userPort.findByUserId(userId, transactionId);

        if (userModel.isEmpty()) {
            throw new InternalException("The client is not available or does not exist", transactionId);
        }

        UserEntity user = userModel.get();

        if (!user.getVerified()) {
            throw new InternalException("Currently your account is not validated, please validate", transactionId);
        }

        ServerModel serverAvailable = serverPort.findByNameAndVersionAndStatusIsTrue(serverName, expansion,
                transactionId);

        if (serverAvailable == null) {
            throw new InternalException("It is not possible to register on any server, they are currently not " +
                    "available", transactionId);
        }

        Long accountId = integratorPort.create(username, password, rebuildUsername, serverAvailable,
                user.mapToModelEntity(),
                transactionId);

        AccountGameEntity accountGameEntity = new AccountGameEntity();
        accountGameEntity.setAccountId(accountId);
        accountGameEntity.setServerId(ServerMapper.toEntity(serverAvailable));
        accountGameEntity.setUserId(user);
        accountGameEntity.setStatus(true);
        saveAccountGamePort.save(accountGameEntity, transactionId);
    }
}
