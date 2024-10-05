package com.register.wowlibre.application.services.integrator;

import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.infrastructure.client.*;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.stereotype.*;

import javax.crypto.*;

@Service
public class IntegratorService implements IntegratorPort {

    private final IntegratorClient integratorClient;

    public IntegratorService(IntegratorClient integratorClient) {
        this.integratorClient = integratorClient;
    }

    @Override
    public Long create(String username, String password, boolean rebuildUsername, ServerModel server, UserModel userModel,
                       String transactionId) {

        try {
            byte[] salt = KeyDerivationUtil.generateSalt();
            SecretKey derivedKey = KeyDerivationUtil.deriveKeyFromPassword(server.apiSecret, salt);
            String encryptedMessage = EncryptionUtil.encrypt(password, derivedKey);

            AccountGameCreateDto accountGameCreateDto = AccountGameCreateDto.builder()
                    .username(username)
                    .password(encryptedMessage)
                    .email(userModel.email)
                    .rebuildUsername(rebuildUsername)
                    .userId(userModel.id)
                    .expansion(server.expansion)
                    .salt(salt)
                    .build();

            return integratorClient.createAccountGame(server.ip, accountGameCreateDto, transactionId);
        } catch (Exception e) {
            throw new InternalException("It was not possible to create the account on the server due to a security " +
                    "issue", transactionId);
        }

    }
}
