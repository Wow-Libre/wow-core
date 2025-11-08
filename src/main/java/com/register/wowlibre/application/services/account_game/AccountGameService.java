package com.register.wowlibre.application.services.account_game;

import com.register.wowlibre.domain.dto.account_game.AccountGameDetailDto;
import com.register.wowlibre.domain.dto.account_game.AccountGameStatsDto;
import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;
import com.register.wowlibre.domain.dto.account_game.AccountsGameDto;
import com.register.wowlibre.domain.dto.client.AccountDetailResponse;
import com.register.wowlibre.domain.enums.Expansion;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.exception.UnauthorizedException;
import com.register.wowlibre.domain.mapper.RealmMapper;
import com.register.wowlibre.domain.model.AccountGameModel;
import com.register.wowlibre.domain.model.RealmModel;
import com.register.wowlibre.domain.port.in.account_game.AccountGamePort;
import com.register.wowlibre.domain.port.in.integrator.IntegratorPort;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.domain.port.in.user.UserPort;
import com.register.wowlibre.domain.port.out.account_game.ObtainAccountGamePort;
import com.register.wowlibre.domain.port.out.account_game.SaveAccountGamePort;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.entities.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountGameService implements AccountGamePort {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountGameService.class);

    /**
     * Account Game PORT
     **/
    private final SaveAccountGamePort saveAccountGamePort;
    private final ObtainAccountGamePort obtainAccountGamePort;
    /**
     * USERS PORT
     **/
    private final UserPort userPort;
    /**
     * Realm PORT
     **/
    private final RealmPort realmPort;
    /**
     * EXTERNAL CLIENT PORT
     **/
    private final IntegratorPort integratorPort;

    public AccountGameService(SaveAccountGamePort saveAccountGamePort, ObtainAccountGamePort obtainAccountGamePort,
                              RealmPort realmPort, UserPort userPort,
                              IntegratorPort integratorPort) {
        this.saveAccountGamePort = saveAccountGamePort;
        this.obtainAccountGamePort = obtainAccountGamePort;
        this.realmPort = realmPort;
        this.userPort = userPort;
        this.integratorPort = integratorPort;
    }

    @Override
    public void create(Long userId, String realmName, Integer expansionId, String username,
                       String gameMail, String password,
                       String transactionId) {

        Optional<UserEntity> userModel = userPort.findByUserId(userId, transactionId);

        if (userModel.isEmpty()) {
            LOGGER.error("[AccountGameService] [create] User not found - userId: {}, transactionId: {}", userId, transactionId);
            throw new UnauthorizedException("The client is not available or does not exist", transactionId);
        }

        UserEntity user = userModel.get();

        RealmModel realmModel = realmPort.findByNameAndVersionAndStatusIsTrue(realmName, expansionId, transactionId);

        if (realmModel == null) {
            LOGGER.error("[AccountGameService] [create] Realm not found or unavailable - realmName: {}, expansionId: {}, userId: {}, transactionId: {}",
                    realmName, expansionId, userId, transactionId);
            throw new InternalException("It is not possible to register on any realm, they are currently not " +
                    "available", transactionId);
        }

        int existingAccountsCount = obtainAccountGamePort.findByUserIdAndRealmId(userId, realmModel.id, transactionId).size();
        if (existingAccountsCount >= 20) {
            LOGGER.error("[AccountGameService] [create] Maximum accounts limit reached - userId: {}, realmId: {}, " +
                    "existingAccounts: {}, transactionId: {}", userId, realmModel.id, existingAccountsCount, transactionId);
            throw new InternalException("You cannot create more than 20 accounts per realm", transactionId);
        }

        final String gameEMail = gameMail != null && !gameMail.isBlank() ? gameMail : user.getEmail();

        Long accountId = integratorPort.createAccount(realmModel.ip, realmModel.apiSecret, realmModel.expansion,
                username, password, gameEMail, user.getId(), transactionId);

        AccountGameEntity accountGameEntity = new AccountGameEntity();
        accountGameEntity.setAccountId(accountId);
        accountGameEntity.setRealmId(RealmMapper.toEntity(realmModel));
        accountGameEntity.setUserId(user);
        accountGameEntity.setUsername(username);
        accountGameEntity.setStatus(true);
        accountGameEntity.setGameEmail(gameEMail);
        saveAccountGamePort.save(accountGameEntity, transactionId);
    }


    @Override
    public AccountsGameDto accounts(Long userId, int page, int size, String searchUsername, String realmName,
                                    String transactionId) {
        if (size > 30) {
            size = 30;
        }

        Long sizeAccounts = obtainAccountGamePort.accounts(userId);
        List<AccountGameModel> accountsGame = new ArrayList<>();

        if (sizeAccounts > 0) {
            if (searchUsername != null || realmName != null) {
                accountsGame = obtainAccountGamePort.findByUserIdAndRealmNameAndUsernameStatusIsTrue(userId, page,
                        size, realmName, searchUsername, transactionId).stream().map(this::mapToModel).toList();
            } else {
                accountsGame = obtainAccountGamePort.findByUserIdAndStatusIsTrue(userId,
                        page, size, transactionId).stream().map(this::mapToModel).toList();
            }
        }


        return new AccountsGameDto(accountsGame, sizeAccounts);
    }


    @Override
    public AccountsGameDto accounts(Long userId, Long realmId, String transactionId) {

        List<AccountGameModel> accountsGame = obtainAccountGamePort.findByUserIdAndRealmId(userId, realmId,
                transactionId).stream().map(this::mapToModel).toList();

        return new AccountsGameDto(accountsGame, (long) accountsGame.size());
    }

    @Override
    public AccountVerificationDto verifyAccount(Long userId, Long accountId, Long realmId, String transactionId) {

        Optional<RealmEntity> realm = realmPort.findById(realmId, transactionId);

        if (realm.isEmpty()) {
            LOGGER.error("[AccountGameService] [verifyAccount] Realm not found - realmId: {}, userId: {}, accountId: {}, transactionId: {}",
                    realmId, userId, accountId, transactionId);
            throw new InternalException("The realm where your character is currently located is not available",
                    transactionId);
        }

        Optional<AccountGameEntity> accountGame =
                obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId,
                        realm.get().getId(), transactionId);

        if (accountGame.isEmpty()) {
            LOGGER.error("[AccountGameService] [verifyAccount] Account game not found or inactive - userId: {}, accountId: {}, realmId: {}, transactionId: {}",
                    userId, accountId, realmId, transactionId);
            throw new InternalException("Currently your account is not found or is not available, please contact " +
                    "support", transactionId);
        }
        return new AccountVerificationDto(realm.get(), accountGame.get());
    }

    @Override
    public AccountGameDetailDto account(Long userId, Long accountId, Long realmId, String transactionId) {

        Optional<RealmEntity> realm = realmPort.findById(realmId, transactionId);

        if (realm.isEmpty() || !realm.get().isStatus()) {
            LOGGER.error("[AccountGameService] [account] Realm not found or inactive - realmId: {}, userId: {}, accountId: {}, transactionId: {}",
                    realmId, userId, accountId, transactionId);
            throw new InternalException("The realm where your character is currently located is not available",
                    transactionId);
        }

        final RealmEntity realmDetail = realm.get();

        Optional<AccountGameEntity> accountGame =
                obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                        transactionId);

        if (accountGame.isEmpty()) {
            LOGGER.error("[AccountGameService] [account] Account game not found or inactive - userId: {}, accountId: {}, realmId: {}, transactionId: {}",
                    userId, accountId, realmId, transactionId);
            throw new InternalException("Currently your account is not found or is not available, please contact " +
                    "support", transactionId);
        }

        AccountDetailResponse account = integratorPort.account(realmDetail.getHost(), realmDetail.getJwt(),
                accountId, transactionId);


        return AccountGameDetailDto.builder()
                .username(account.username())
                .expansion(account.expansion())
                .id(account.id())
                .email(account.email())
                .os(account.os())
                .lastIp(account.lastIp())
                .lastLogin(account.lastLogin())
                .joinDate(account.joinDate())
                .muteBy(account.muteBy())
                .muteReason(account.muteReason())
                .mute(account.mute())
                .online(account.online())
                .realm(realmDetail.getName())
                .failedLogins(account.failedLogins())
                .accountBanned(account.accountBanned()).build();
    }

    @Override
    public void desactive(List<Long> id, Long userId, String transactionId) {
        id.forEach(account -> {
                    Optional<AccountGameEntity> accountGame =
                            obtainAccountGamePort.findByIdAndUserId(account, userId, transactionId);
                    if (accountGame.isEmpty()) {
                        LOGGER.error("[AccountGameService] [desactive] Account not found or does not belong to user - accountId: {}, userId: {}, transactionId: {}",
                                account, userId, transactionId);
                        throw new InternalException("Invalid desactive user", transactionId);
                    }
                    AccountGameEntity accountDesactive = accountGame.get();
                    accountDesactive.setStatus(false);
                    saveAccountGamePort.save(accountDesactive, transactionId);
                }
        );
    }

    @Override
    public AccountGameStatsDto stats(Long userId, String transactionId) {
        long totalAccounts = obtainAccountGamePort.countActiveAccountsByUserId(userId, transactionId);
        long totalRealms = obtainAccountGamePort.countDistinctRealmsByUserId(userId, transactionId);
        return new AccountGameStatsDto(totalAccounts, totalRealms);
    }


    private AccountGameModel mapToModel(AccountGameEntity accountGameEntity) {
        boolean status = accountGameEntity.isStatus() && accountGameEntity.getRealmId().isStatus();
        Integer expansionId = accountGameEntity.getRealmId().getExpansionId();
        Expansion expansion = Expansion.getById(expansionId);
        return new AccountGameModel(accountGameEntity.getId(), accountGameEntity.getUsername(),
                accountGameEntity.getAccountId(), accountGameEntity.getGameEmail(),
                accountGameEntity.getRealmId().getName(), accountGameEntity.getRealmId().getId(),
                expansion.getName(), expansionId, accountGameEntity.getRealmId().getAvatarUrl(),
                accountGameEntity.getRealmId().getWeb(), status, accountGameEntity.getRealmId().getRealmlist());
    }
}
