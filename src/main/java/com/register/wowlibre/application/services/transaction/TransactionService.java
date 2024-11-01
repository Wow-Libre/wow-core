package com.register.wowlibre.application.services.transaction;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class TransactionService implements TransactionPort {

    private final ServerPort serverPort;
    private final IntegratorPort integratorPort;

    public TransactionService(ServerPort serverPort, IntegratorPort integratorPort) {
        this.serverPort = serverPort;
        this.integratorPort = integratorPort;
    }


    @Override
    public void purchase(Long serverId, Long userId, Long accountId, String reference, List<ItemQuantityModel> items,
                         String transactionId) {

        Optional<ServerEntity> serverModel = serverPort.findById(serverId, transactionId);

        if (serverModel.isEmpty()) {
            throw new InternalException("Server Not Vailable", transactionId);
        }

        ServerEntity server = serverModel.get();

        integratorPort.purchase(server.getIp(), server.getJwt(), userId, accountId, reference, items, transactionId);
    }
}
