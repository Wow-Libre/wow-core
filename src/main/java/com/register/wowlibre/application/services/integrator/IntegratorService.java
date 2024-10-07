package com.register.wowlibre.application.services.integrator;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.infrastructure.client.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import javax.crypto.*;

@Service
public class IntegratorService implements IntegratorPort {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(IntegratorService.class);

    private final IntegratorClient integratorClient;

    public IntegratorService(IntegratorClient integratorClient) {
        this.integratorClient = integratorClient;
    }

    @Override
    public Long create(String username, String password, ServerModel server, UserModel userModel,
                       String transactionId) {

        try {
            byte[] salt = KeyDerivationUtil.generateSalt();
            SecretKey derivedKey = KeyDerivationUtil.deriveKeyFromPassword(server.apiSecret, salt);
            String encryptedMessage = EncryptionUtil.encrypt(password, derivedKey);

            AccountGameCreateDto accountGameCreateDto = AccountGameCreateDto.builder()
                    .username(username)
                    .password(encryptedMessage)
                    .email(userModel.email)
                    .userId(userModel.id)
                    .expansion(server.expansion)
                    .salt(salt)
                    .build();

            return integratorClient.createAccountGame(server.ip, accountGameCreateDto, transactionId);
        } catch (Exception e) {
            LOGGER.error("It was not possible to create the account on the server {} userId: {}", server.name,
                    userModel.id);
            throw new InternalException("It was not possible to create the account on the server due to a security " +
                    "issue", transactionId);
        }

    }

    @Override
    public CharactersDto characters(ServerModel server, Long accountId, Long userId, String transactionId) {
        CharactersResponse response = integratorClient.characters(server.ip, server.jwt, accountId,
                transactionId);

        if (response == null) {
            LOGGER.error("It was not possible to get the characters from the server. name: {} Host: {} userId: {}",
                    server.name, server.ip, userId);
            throw new InternalException("It was not possible to get the characters from the server.", transactionId);
        }

        CharactersDto characters = new CharactersDto();
        characters.setCharacters(response.getCharacters());
        characters.setTotalQuantity(response.getTotalQuantity());

        return characters;
    }

    @Override
    public AccountDetailResponse account(String host, String jwt, Long accountId, String transactionId) {
        AccountDetailResponse accountDetailResponse = integratorClient.account(host, jwt, accountId, transactionId);

        if (accountDetailResponse == null) {
            LOGGER.error("Could not get account details for this server.  Host: {} accountId: {} " +
                    "transactionId {}", host, accountId, transactionId);
            throw new InternalException("It was not possible to obtain the account details for this server",
                    transactionId);
        }

        return accountDetailResponse;
    }

    @Override
    public MailsDto mails(String host, String jwt, Long characterId, String transactionId) {
        MailsResponse mailsResponse = integratorClient.mails(host, jwt, characterId, transactionId);

        if (mailsResponse == null) {
            LOGGER.error("It was not possible to obtain the emails for this server.  Host: {} characterId: {} " +
                    "transactionId {}", host, characterId, transactionId);
            throw new InternalException("It was not possible to obtain the emails for this server",
                    transactionId);
        }

        return new MailsDto(mailsResponse.getMails(), mailsResponse.getSize());
    }

    @Override
    public void deleteFriend(String host, String jwt, Long characterId, Long friendId, Long accountId,
                             String transactionId) {
        integratorClient.deleteFriend(host, jwt, characterId, friendId, accountId, transactionId);
    }

    @Override
    public CharacterSocialDto friends(String host, String jwt, Long characterId, String transactionId) {
        CharacterSocialResponse characterSocialResponse = integratorClient.friends(host, jwt, characterId,
                transactionId);

        if (characterSocialResponse == null) {
            LOGGER.error("It was not possible to obtain the list of friends on this server.  Host: {} characterId: {} " +
                    "transactionId {}", host, characterId, transactionId);
            throw new InternalException("It was not possible to obtain the list of friends on this server",
                    transactionId);
        }

        return new CharacterSocialDto(characterSocialResponse.getFriends(), characterSocialResponse.getTotalQuantity());
    }

    @Override
    public void changePassword(String host, String jwt, Long accountId, String transactionId) {

    }
}
