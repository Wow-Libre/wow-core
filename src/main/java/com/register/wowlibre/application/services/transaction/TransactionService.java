package com.register.wowlibre.application.services.transaction;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class TransactionService implements TransactionPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    private final IntegratorPort integratorPort;
    private final AccountGamePort accountGamePort;

    public TransactionService(IntegratorPort integratorPort, AccountGamePort accountGamePort) {
        this.integratorPort = integratorPort;
        this.accountGamePort = accountGamePort;
    }


    @Override
    public void purchase(Long serverId, Long userId, Long accountId, String reference, List<ItemQuantityModel> items,
                         Double amount, String transactionId) {

        AccountVerificationDto accountVerificationDto = accountGamePort.verifyAccount(userId, accountId, serverId,
                transactionId);

        ServerEntity server = accountVerificationDto.server();

        if (server == null) {
            LOGGER.error("Server is not available");
            throw new InternalException("Server is not available", transactionId);
        }

        integratorPort.purchase(server.getIp(), server.getJwt(), userId, accountId, reference, items, amount,
                transactionId);
    }

    @Override
    public void sendSubscriptionBenefits(Long serverId, Long userId, Long accountId, Long characterId,
                                         List<ItemQuantityModel> items, String benefitType, Double amount,
                                         String transactionId) {

        AccountVerificationDto accountVerificationDto = accountGamePort.verifyAccount(userId, accountId, serverId,
                transactionId);

        ServerEntity server = accountVerificationDto.server();

        if (server == null) {
            LOGGER.error("Server is not available");
            throw new InternalException("Server is not available", transactionId);
        }

        integratorPort.sendBenefitsPremium(server.getIp(), server.getJwt(), userId, accountId, characterId, items,
                benefitType, amount, transactionId);

    }
}
