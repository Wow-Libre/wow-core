package com.register.wowlibre.application.services.account_game;

import com.register.wowlibre.domain.dto.account_game.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.mapper.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.account_game.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.machine.*;
import com.register.wowlibre.domain.port.in.realm.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.out.account_game.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class AccountGameService implements AccountGamePort {
    public static final int MAXIMUM_NUMBER_OF_ACCOUNTS_ALLOWED = 20;
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
    private final MachinePort machinePort;

    public AccountGameService(SaveAccountGamePort saveAccountGamePort, ObtainAccountGamePort obtainAccountGamePort,
                              RealmPort realmPort, UserPort userPort,
                              IntegratorPort integratorPort, MachinePort machinePort) {
        this.saveAccountGamePort = saveAccountGamePort;
        this.obtainAccountGamePort = obtainAccountGamePort;
        this.realmPort = realmPort;
        this.userPort = userPort;
        this.integratorPort = integratorPort;
        this.machinePort = machinePort;
    }

    @Override
    public void create(Long userId, String realmName, Integer expansionId, String username,
                       String gameMail, String password,
                       String transactionId) {

        final Optional<UserEntity> userModel = userPort.findByUserId(userId, transactionId);

        if (userModel.isEmpty()) {
            LOGGER.error("[AccountGameService] [create] User not found - userId: {}, transactionId: {}", userId,
                    transactionId);
            throw new UnauthorizedException("The client is not available or does not exist", transactionId);
        }

        final UserEntity user = userModel.get();
        final RealmModel realmModel = realmPort.findByNameAndVersionAndStatusIsTrue(realmName, expansionId,
                transactionId);

        if (realmModel == null) {
            LOGGER.error("[AccountGameService] [create] Realm not found or unavailable - realmName: {}, expansionId: " +
                            "{}, userId: {}, transactionId: {}",
                    realmName, expansionId, userId, transactionId);
            throw new InternalException("It is not possible to register on any realm, they are currently not " +
                    "available", transactionId);
        }

        final int existingAccountsCount = obtainAccountGamePort.findByUserIdAndRealmId(userId, realmModel.id,
                transactionId).size();

        if (existingAccountsCount >= MAXIMUM_NUMBER_OF_ACCOUNTS_ALLOWED) {
            LOGGER.error("[AccountGameService] [create] Maximum accounts limit reached - userId: {}, realmId: {}, " +
                            "existingAccounts: {}, transactionId: {}", userId, realmModel.id, existingAccountsCount,
                    transactionId);
            throw new InternalException("You cannot create more than 20 accounts per realm", transactionId);
        }

        final String gameEMail = gameMail != null && !gameMail.isBlank() ? gameMail : user.getEmail();

        final Long accountId = integratorPort.createAccount(realmModel.ip, realmModel.apiSecret, realmModel.expansion,
                username, password, gameEMail, user.getId(), transactionId);

        AccountGameEntity accountGameEntity = new AccountGameEntity();
        accountGameEntity.setAccountId(accountId);
        accountGameEntity.setRealmId(RealmMapper.toEntity(realmModel));
        accountGameEntity.setUserId(user);
        accountGameEntity.setUsername(username);
        accountGameEntity.setStatus(true);
        accountGameEntity.setGameEmail(gameEMail);
        saveAccountGamePort.save(accountGameEntity, transactionId);
        machinePort.points(userId, accountId, realmModel.id, transactionId);
    }


    @Override
    public AccountsGameDto accounts(Long userId, int page, int size, String searchUsername, String realmName,
                                    String transactionId) {

        final int pageSize = Math.min(size, 30);
        final Long totalAccounts = obtainAccountGamePort.accounts(userId);

        if (totalAccounts == 0) {
            return new AccountsGameDto(List.of(), 0L);
        }

        final boolean hasFilters =
                (searchUsername != null && !searchUsername.isBlank()) ||
                        (realmName != null && !realmName.isBlank());

        final List<AccountGameModel> accountsGame = hasFilters
                ? obtainAccountGamePort
                .findByUserIdAndRealmNameAndUsernameStatusIsTrue(
                        userId, page, pageSize, realmName, searchUsername, transactionId)
                .stream()
                .map(this::mapToModel)
                .toList()
                : obtainAccountGamePort
                .findByUserIdAndStatusIsTrue(
                        userId, page, pageSize, transactionId)
                .stream()
                .map(this::mapToModel)
                .toList();

        return new AccountsGameDto(accountsGame, totalAccounts);
    }


    @Override
    public AccountsGameDto accounts(Long userId, Long realmId, String transactionId) {

        List<AccountGameModel> accountsGame = obtainAccountGamePort.findByUserIdAndRealmId(userId, realmId,
                transactionId).stream().map(this::mapToModel).toList();

        return new AccountsGameDto(accountsGame, (long) accountsGame.size());
    }


    @Override
    public AccountGameDetailDto account(Long userId, Long accountId, Long realmId, String transactionId) {

        final Optional<RealmEntity> realm = realmPort.findById(realmId, transactionId);

        if (realm.isEmpty() || !realm.get().isStatus()) {
            LOGGER.error("[AccountGameService] [account] Realm not found or inactive - realmId: {}, userId: {}, "
                    + "accountId: {}, transactionId: {}", realmId, userId, accountId, transactionId);
            throw new InternalException("The realm where your character is currently located is not available",
                    transactionId);
        }

        final RealmEntity realmDetail = realm.get();

        final Optional<AccountGameEntity> accountGame =
                obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                        transactionId);

        if (accountGame.isEmpty()) {
            LOGGER.error("[AccountGameService] [account] Account game not found or inactive - userId: {}, accountId: " +
                            "{}, realmId: {}, transactionId: {}",
                    userId, accountId, realmId, transactionId);
            throw new InternalException("Currently your account is not found or is not available, please contact " +
                    "support", transactionId);
        }

        final AccountDetailResponse account = integratorPort.account(realmDetail.getHost(), realmDetail.getJwt(),
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
    public void deactivate(List<Long> id, Long userId, String transactionId) {
        id.forEach(account -> {
                    Optional<AccountGameEntity> accountGame =
                            obtainAccountGamePort.findByIdAndUserId(account, userId, transactionId);
                    if (accountGame.isEmpty()) {
                        LOGGER.error("[AccountGameService] [desactive] Account not found or does not belong to user -"
                                + " accountId: {}, userId: {}, transactionId: {}", account, userId, transactionId);
                        throw new InternalException("Invalid desactive user", transactionId);
                    }
                    AccountGameEntity deactivate = accountGame.get();
                    deactivate.setStatus(false);
                    saveAccountGamePort.save(deactivate, transactionId);
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
        final boolean status = accountGameEntity.isStatus() && accountGameEntity.getRealmId().isStatus();
        final Integer expansionId = accountGameEntity.getRealmId().getExpansionId();
        final Expansion expansion = Expansion.getById(expansionId);
        return new AccountGameModel(accountGameEntity.getId(), accountGameEntity.getUsername(),
                accountGameEntity.getAccountId(), accountGameEntity.getGameEmail(),
                accountGameEntity.getRealmId().getName(), accountGameEntity.getRealmId().getId(),
                expansion.getName(), expansionId, accountGameEntity.getRealmId().getAvatarUrl(),
                accountGameEntity.getRealmId().getWeb(), status, accountGameEntity.getRealmId().getRealmlist());
    }
}
