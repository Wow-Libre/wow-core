package com.register.wowlibre.application.services.account_game;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.out.account_game.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class AccountGameService implements AccountGamePort {
    private final ObtainAccountGamePort obtainAccountGamePort;
    private final SaveAccountGamePort saveAccountGamePort;
    private final ServerPort serverPort;
    private final UserPort userPort;

    public AccountGameService(ObtainAccountGamePort obtainAccountGamePort, SaveAccountGamePort saveAccountGamePort,
                              ServerPort serverPort, UserPort userPort) {
        this.obtainAccountGamePort = obtainAccountGamePort;
        this.saveAccountGamePort = saveAccountGamePort;
        this.serverPort = serverPort;
        this.userPort = userPort;
    }

    @Override
    public List<AccountGameModel> accounts(Long userId, String transactionId) {
        return null;
    }

    @Override
    public void create(Long userId, String serverName, String expansion, Long accountId, String transactionId) {
        UserModel userModel = userPort.findByUserId(userId, transactionId);

        if (userModel == null) {
            throw new InternalException("The client is not available or does not exist", transactionId);
        }

        if (!userModel.verified) {
            throw new InternalException("Currently your account is not validated, please validate", transactionId);
        }

        List<ServerModel> serverAvailable = serverPort.findByStatusIsTrue(transactionId);

        if (serverAvailable.isEmpty()) {
            throw new InternalException("It is not possible to register on any server, they are currently not " +
                    "available", transactionId);
        }

    }

    @Async
    public void registerServers(List<ServerModel> servers) {

    }
}
