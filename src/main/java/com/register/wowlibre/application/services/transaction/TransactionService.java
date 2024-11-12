package com.register.wowlibre.application.services.transaction;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class TransactionService implements TransactionPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    private final ServerPort serverPort;
    private final IntegratorPort integratorPort;

    public TransactionService(ServerPort serverPort, IntegratorPort integratorPort) {
        this.serverPort = serverPort;
        this.integratorPort = integratorPort;
    }


    @Override
    public void purchase(Long serverId, Long userId, Long accountId, String reference, List<ItemQuantityModel> items,
                         Double amount, String transactionId) {

        Optional<ServerEntity> serverModel = serverPort.findById(serverId, transactionId);

        if (serverModel.isEmpty()) {
            LOGGER.error("Server is not available");
            throw new InternalException("Server is not available", transactionId);
        }

        ServerEntity server = serverModel.get();

        integratorPort.purchase(server.getIp(), server.getJwt(), userId, accountId, reference, items, amount,
                transactionId);
    }
}
