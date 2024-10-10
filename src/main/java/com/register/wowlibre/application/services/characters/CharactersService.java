package com.register.wowlibre.application.services.characters;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.characters.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class CharactersService implements CharactersPort {
    private final IntegratorPort integratorService;
    private final AccountGamePort accountGamePort;
    private final PasswordEncoder passwordEncoder;

    public CharactersService(IntegratorPort integratorService, AccountGamePort accountGamePort,
                             PasswordEncoder passwordEncoder) {
        this.integratorService = integratorService;
        this.accountGamePort = accountGamePort;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public CharactersDto characters(Long userId, Long accountId, Long serverId, String transactionId) {

        AccountVerificationDto accountVerificationDto = accountGamePort.verify(userId, accountId, serverId,
                transactionId);

        return integratorService.characters(accountVerificationDto.server().getIp(),
                accountVerificationDto.server().getJwt(),
                accountId, userId, transactionId);
    }

    @Override
    public void deleteFriend(Long userId, Long accountId, Long serverId, Long characterId, Long friendId,
                             String transactionId) {

        AccountVerificationDto accountVerificationDto = accountGamePort.verify(userId, accountId, serverId,
                transactionId);


        integratorService.deleteFriend(accountVerificationDto.server().getIp(),
                accountVerificationDto.server().getJwt(),
                characterId, friendId, accountId, userId, transactionId);
    }


    @Override
    public MailsDto mails(Long userId, Long accountId, Long serverId, Long characterId, String transactionId) {

        AccountVerificationDto accountVerificationDto = accountGamePort.verify(userId, accountId, serverId,
                transactionId);

        return integratorService.mails(accountVerificationDto.server().getIp(),
                accountVerificationDto.server().getJwt(), characterId, transactionId);
    }

    @Override
    public CharacterSocialDto friends(Long userId, Long accountId, Long serverId, Long characterId,
                                      String transactionId) {

        AccountVerificationDto accountVerificationDto = accountGamePort.verify(userId, accountId, serverId,
                transactionId);

        return integratorService.friends(accountVerificationDto.server().getIp(),
                accountVerificationDto.server().getJwt(), characterId, transactionId);
    }


    @Override
    public void changePassword(Long userId, Long accountId, Long serverId, String password, String newPassword,
                               String transactionId) {

        AccountVerificationDto accountVerificationDto = accountGamePort.verify(userId, accountId, serverId,
                transactionId);

        AccountGameEntity accountGameModel = accountVerificationDto.accountGame();
        Long userIdAccount = accountGameModel.getUserId().getId();

        if (!passwordEncoder.matches(password, accountVerificationDto.accountGame().getUserId().getPassword())) {
            throw new InternalException("The password is invalid", transactionId);
        }

        integratorService.changePassword(accountVerificationDto.server().getIp(),
                accountVerificationDto.server().getApiSecret(),
                accountVerificationDto.server().getJwt(),
                accountId, userIdAccount, newPassword, transactionId);

    }

    @Override
    public List<CharacterProfessionsDto> professions(Long userId, Long accountId, Long serverId, Long characterId,
                                                     String transactionId) {

        AccountVerificationDto accountVerificationDto = accountGamePort.verify(userId, accountId, serverId,
                transactionId);

        return integratorService.professions(accountVerificationDto.server().getIp(),
                accountVerificationDto.server().getJwt(), accountId, characterId, transactionId);
    }

    @Override
    public void sendLevel(Long userId, Long accountId, Long serverId, Long characterId, Long friendId, Integer level,
                          String transactionId) {

        AccountVerificationDto verifyData = accountGamePort.verify(userId, accountId, serverId,
                transactionId);

        final ServerEntity serverModel = verifyData.server();
        final AccountGameEntity accountGame = verifyData.accountGame();

        integratorService.sendLevel(serverModel.getIp(), serverModel.getJwt(), accountGame.getAccountId(),
                accountGame.getUserId().getId(), characterId, friendId, level, 1.0, transactionId);
    }

    @Override
    public void sendMoney(Long userId, Long accountId, Long serverId, Long characterId, Long friendId, Long money,
                          String transactionId) {
        AccountVerificationDto verifyData = accountGamePort.verify(userId, accountId, serverId,
                transactionId);

        final ServerEntity serverModel = verifyData.server();
        final AccountGameEntity accountGame = verifyData.accountGame();

        integratorService.sendMoney(serverModel.getIp(), serverModel.getJwt(), accountGame.getAccountId(),
                accountGame.getUserId().getId(), characterId, friendId, money, 1.0, transactionId);
    }

}
