package com.register.wowlibre.service;

import com.register.wowlibre.application.services.account_game.AccountGameService;
import com.register.wowlibre.domain.dto.account_game.AccountGameDetailDto;
import com.register.wowlibre.domain.dto.account_game.AccountGameStatsDto;
import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;
import com.register.wowlibre.domain.dto.account_game.AccountsGameDto;
import com.register.wowlibre.domain.dto.client.AccountDetailResponse;
import com.register.wowlibre.domain.enums.Expansion;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.exception.UnauthorizedException;
import com.register.wowlibre.domain.model.RealmModel;
import com.register.wowlibre.domain.port.in.integrator.IntegratorPort;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.domain.port.in.user.UserPort;
import com.register.wowlibre.domain.port.out.account_game.ObtainAccountGamePort;
import com.register.wowlibre.domain.port.out.account_game.SaveAccountGamePort;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.entities.UserEntity;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountGameServiceTest extends BaseTest {

    @Mock
    private SaveAccountGamePort saveAccountGamePort;
    @Mock
    private ObtainAccountGamePort obtainAccountGamePort;
    @Mock
    private RealmPort realmPort;
    @Mock
    private UserPort userPort;
    @Mock
    private IntegratorPort integratorPort;

    private AccountGameService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AccountGameService(saveAccountGamePort, obtainAccountGamePort, realmPort, userPort,
                integratorPort);
    }

    @Test
    void testAccounts_returnsAccountListSuccessfully() {
        Long userId = 1L;
        Long realmId = 2L;
        String transactionId = "tx-004";


        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(createSampleUserEntity()));

        RealmEntity realmEntity = new RealmEntity();
        realmEntity.setStatus(true);
        realmEntity.setId(realmId);
        realmEntity.setExpansionId(2);


        AccountGameEntity accountEntity = new AccountGameEntity();
        accountEntity.setAccountId(123L);
        accountEntity.setUsername("testaccount");
        accountEntity.setStatus(true);
        accountEntity.setRealmId(realmEntity);
        accountEntity.setUserId(createSampleUserEntity());


        when(obtainAccountGamePort.findByUserIdAndRealmId(userId, realmId, transactionId))
                .thenReturn(List.of(accountEntity));

        AccountsGameDto result = service.accounts(userId, realmId, transactionId);

        assertNotNull(result);
        assertEquals(1, result.getAccounts().size());
        assertEquals("testaccount", result.getAccounts().get(0).username());
    }


    @Test
    void testCreateAccount_userReachedMaxAccounts() {
        Long userId = 1L;
        String serverName = "Azeroth";
        Integer expansionId = 2;
        String username = "testuser";
        String password = "123456";
        String transactionId = "tx-002";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@mail.com");

        RealmModel realmModel = RealmModel.builder()
                .id(100L)
                .name(serverName)
                .ip("localhost")
                .apiSecret("secret")
                .expansion(2)
                .build();

        // Simula 20 cuentas ya creadas
        List<AccountGameEntity> existingAccounts = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            existingAccounts.add(new AccountGameEntity());
        }

        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(userEntity));
        when(realmPort.findByNameAndVersionAndStatusIsTrue(serverName, expansionId, transactionId)).thenReturn(realmModel);
        when(obtainAccountGamePort.findByUserIdAndRealmId(userId, realmModel.id, transactionId)).thenReturn(existingAccounts);

        assertThrows(InternalException.class, () ->
                service.create(userId, serverName, expansionId, username, password, "gameMail", transactionId)
        );
    }


    @Test
    void testCreateAccount_server() {
        Long userId = 1L;
        String serverName = "Azeroth";
        Integer expansionId = 2;
        String username = "testuser";
        String password = "123456";
        String gameMail = "gameMail";
        String transactionId = "tx-001";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@mail.com");


        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(userEntity));
        when(realmPort.findByNameAndVersionAndStatusIsTrue(serverName, expansionId, transactionId)).thenReturn(null);
        when(integratorPort.createAccount(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(999L);

        assertThrows(InternalException.class, () ->
                service.create(userId, serverName, expansionId, username, password, gameMail, transactionId)
        );
    }


    @Test
    void testCreateAccount_serverNotFound() {
        Long userId = 1L;
        String serverName = "Azeroth";
        Integer expansionId = 2;
        String username = "testuser";
        String password = "123456";
        String transactionId = "tx-001";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@mail.com");
        RealmModel realmModel = RealmModel.builder().ip("localhost").build();

        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(userEntity));
        when(realmPort.findByNameAndVersionAndStatusIsTrue(serverName, expansionId, transactionId)).thenReturn(null);
        when(obtainAccountGamePort.findByUserIdAndRealmId(userId, realmModel.id, transactionId)).thenReturn(Collections.emptyList());
        when(integratorPort.createAccount(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(999L);

        assertThrows(InternalException.class, () ->
                service.create(userId, serverName, expansionId, username, null, password, transactionId)
        );
    }


    @Test
    void testCreateAccount_success() {
        Long userId = 1L;
        String serverName = "Azeroth";
        Integer expansionId = 2;
        String username = "testuser";
        String password = "123456";
        String transactionId = "tx-001";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@mail.com");

        RealmModel realmModel = RealmModel.builder().ip("localhost").build();

        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(userEntity));
        when(realmPort.findByNameAndVersionAndStatusIsTrue(serverName, expansionId, transactionId)).thenReturn(realmModel);
        when(obtainAccountGamePort.findByUserIdAndRealmId(userId, realmModel.id, transactionId)).thenReturn(Collections.emptyList());
        when(integratorPort.createAccount(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(999L);

        assertDoesNotThrow(() -> service.create(userId, serverName, expansionId, username, "", password,
                transactionId));
        verify(saveAccountGamePort).save(any(AccountGameEntity.class), eq(transactionId));
    }

    @Test
    void testCreateAccount_userNotFound() {
        when(userPort.findByUserId(1L, "tx")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                service.create(1L, "Realm", 1, "user", "", "pass", "tx")
        );

        assertEquals("The client is not available or does not exist", ex.getMessage());
    }

    @Test
    void testAccounts_byPage_success() {
        Long userId = 1L;
        String transactionId = "tx";
        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(new UserEntity()));
        when(obtainAccountGamePort.accounts(userId)).thenReturn(1L);
        when(obtainAccountGamePort.findByUserIdAndStatusIsTrue(userId, 0, 10, transactionId))
                .thenReturn(List.of(buildAccountGameEntity()));

        AccountsGameDto result = service.accounts(userId, 0, 10, null, null, transactionId);

        assertNotNull(result);
        assertEquals(1, result.getAccounts().size());
    }

    @Test
    void testVerifyAccount_success() {
        Long userId = 1L, realmId = 1L, accountId = 101L;
        String transactionId = "tx";

        RealmEntity realm = new RealmEntity();
        realm.setId(realmId);
        realm.setStatus(true);

        AccountGameEntity account = buildAccountGameEntity();

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                transactionId))
                .thenReturn(Optional.of(account));

        AccountVerificationDto result = service.verifyAccount(userId, accountId, realmId, transactionId);

        assertNotNull(result);
        assertEquals(account.getId(), result.accountGame().getId());
    }

    private AccountGameEntity buildAccountGameEntity() {
        AccountGameEntity entity = new AccountGameEntity();
        entity.setId(101L);
        entity.setAccountId(200L);
        entity.setUsername("gameuser");
        entity.setStatus(true);

        UserEntity user = new UserEntity();
        user.setEmail("email@example.com");
        entity.setUserId(user);

        RealmEntity realm = new RealmEntity();
        realm.setId(1L);
        realm.setName("Azeroth");
        realm.setStatus(true);
        realm.setExpansionId(Expansion.WRATH_OF_THE_LICH_KING.getValue());

        entity.setRealmId(realm);
        return entity;
    }

    @Test
    void testAccount_realmNotFoundOrInactive_throwsException() {
        Long userId = 1L, accountId = 100L, realmId = 10L;
        String transactionId = "tx-001";

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.account(userId, accountId, realmId, transactionId)
        );
    }

    @Test
    void testAccount_realmInactive_throwsException() {
        Long userId = 1L, accountId = 100L, realmId = 10L;
        String transactionId = "tx-002";

        RealmEntity realm = new RealmEntity();
        realm.setStatus(false);

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));

        assertThrows(InternalException.class, () ->
                service.account(userId, accountId, realmId, transactionId)
        );
    }

    @Test
    void testAccount_accountNotFound_throwsException() {
        Long userId = 1L, accountId = 100L, realmId = 10L;
        String transactionId = "tx-003";

        RealmEntity realm = new RealmEntity();
        realm.setId(realmId);
        realm.setStatus(true);
        realm.setHost("localhost");
        realm.setJwt("jwt-token");
        realm.setName("Azeroth");

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                transactionId))
                .thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.account(userId, accountId, realmId, transactionId)
        );
    }

    @Test
    void testAccount_success_returnsAccountGameDetailDto() {
        Long userId = 1L, accountId = 100L, realmId = 10L;
        String transactionId = "tx-004";

        RealmEntity realm = new RealmEntity();
        realm.setId(realmId);
        realm.setStatus(true);
        realm.setHost("localhost");
        realm.setJwt("jwt-token");
        realm.setName("Azeroth");

        AccountGameEntity accountGameEntity = new AccountGameEntity();

        AccountDetailResponse accountResponse = new AccountDetailResponse(
                accountId, "testuser", "test@mail.com", "exp1", true, "192.168.1.1",
                LocalDate.now(), "127.0.0.1", "admin", "spam",
                false, LocalDate.now(), "os", null
        );

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                transactionId))
                .thenReturn(Optional.of(accountGameEntity));
        when(integratorPort.account(realm.getHost(), realm.getJwt(), accountId, transactionId))
                .thenReturn(accountResponse);

        AccountGameDetailDto result = service.account(userId, accountId, realmId, transactionId);

        assertNotNull(result);
        assertEquals("test@mail.com", result.email());
        assertEquals("exp1", result.expansion());
        assertEquals(accountId, result.id());
    }

    @Test
    void testVerifyAccount_realmIsEmpty() {
        Long userId = 1L, realmId = 1L, accountId = 101L;
        String transactionId = "tx";

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.verifyAccount(userId, accountId, realmId, transactionId)
        );
    }

    @Test
    void testVerifyAccount_accountGameIsEmpty_shouldThrowException() {
        Long userId = 1L, accountId = 101L, realmId = 1L;
        String transactionId = "tx";

        RealmEntity realm = new RealmEntity();
        realm.setId(realmId);

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                transactionId))
                .thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.verifyAccount(userId, accountId, realmId, transactionId)
        );
    }

    @Test
    void testAccounts_sizeGreaterThan30_shouldLimitTo30() {
        Long userId = 1L;
        int page = 0;
        int size = 50; // mayor a 30
        String searchUsername = null;
        String realmName = null;
        String transactionId = "tx-001";

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(user));
        when(obtainAccountGamePort.accounts(userId)).thenReturn(0L);

        AccountsGameDto result = service.accounts(userId, page, size, searchUsername, realmName, transactionId);

        assertNotNull(result);
        assertEquals(0, result.getAccounts().size());
        assertEquals(0, result.getSize());
    }


    @Test
    void testAccounts_withSearchFilters_shouldCallFilteredQuery() {
        Long userId = 1L;
        int page = 0;
        int size = 10;
        String searchUsername = "testuser";
        String realmName = "Azeroth";
        String transactionId = "tx-003";

        UserEntity user = new UserEntity();
        user.setId(userId);


        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(user));
        when(obtainAccountGamePort.accounts(userId)).thenReturn(1L);
        when(obtainAccountGamePort.findByUserIdAndRealmNameAndUsernameStatusIsTrue(userId, page, size, realmName,
                searchUsername, transactionId))
                .thenReturn(List.of(buildAccountGameEntity()));

        when(userPort.findByUserId(userId, transactionId)).thenReturn(Optional.of(user));
        when(obtainAccountGamePort.accounts(userId)).thenReturn(1L);
        when(obtainAccountGamePort.findByUserIdAndRealmNameAndUsernameStatusIsTrue(userId, page, size, realmName,
                searchUsername, transactionId))
                .thenReturn(List.of(buildAccountGameEntity()));

        // Aquí puedes usar un `spy` para verificar si se llama mapToModel correctamente o simular directamente su
        // salida

        AccountsGameDto result = service.accounts(userId, page, size, searchUsername, realmName, transactionId);

        assertNotNull(result);
        assertEquals(1, result.getAccounts().size());
        assertEquals(1L, result.getSize());
    }

    @Test
    void testStats_success_returnsAccountGameStatsDto() {
        Long userId = 1L;
        String transactionId = "tx-stats-001";
        long expectedTotalAccounts = 5L;
        long expectedTotalRealms = 2L;

        when(obtainAccountGamePort.countActiveAccountsByUserId(userId, transactionId))
                .thenReturn(expectedTotalAccounts);
        when(obtainAccountGamePort.countDistinctRealmsByUserId(userId, transactionId))
                .thenReturn(expectedTotalRealms);

        AccountGameStatsDto result = service.stats(userId, transactionId);

        assertNotNull(result);
        assertEquals(expectedTotalAccounts, result.getTotalAccounts());
        assertEquals(expectedTotalRealms, result.getTotalRealms());
        verify(obtainAccountGamePort).countActiveAccountsByUserId(userId, transactionId);
        verify(obtainAccountGamePort).countDistinctRealmsByUserId(userId, transactionId);
    }

    @Test
    void testStats_zeroAccounts_returnsZeroStats() {
        Long userId = 1L;
        String transactionId = "tx-stats-002";

        when(obtainAccountGamePort.countActiveAccountsByUserId(userId, transactionId)).thenReturn(0L);
        when(obtainAccountGamePort.countDistinctRealmsByUserId(userId, transactionId)).thenReturn(0L);

        AccountGameStatsDto result = service.stats(userId, transactionId);

        assertNotNull(result);
        assertEquals(0L, result.getTotalAccounts());
        assertEquals(0L, result.getTotalRealms());
    }

    @Test
    void testDesactive_success_deactivatesAccounts() {
        Long userId = 1L;
        String transactionId = "tx-desactive-001";
        List<Long> accountIds = List.of(100L, 200L, 300L);

        AccountGameEntity account1 = new AccountGameEntity();
        account1.setId(100L);
        account1.setStatus(true);
        account1.setUserId(createSampleUserEntity());

        AccountGameEntity account2 = new AccountGameEntity();
        account2.setId(200L);
        account2.setStatus(true);
        account2.setUserId(createSampleUserEntity());

        AccountGameEntity account3 = new AccountGameEntity();
        account3.setId(300L);
        account3.setStatus(true);
        account3.setUserId(createSampleUserEntity());

        when(obtainAccountGamePort.findByIdAndUserId(100L, userId, transactionId))
                .thenReturn(Optional.of(account1));
        when(obtainAccountGamePort.findByIdAndUserId(200L, userId, transactionId))
                .thenReturn(Optional.of(account2));
        when(obtainAccountGamePort.findByIdAndUserId(300L, userId, transactionId))
                .thenReturn(Optional.of(account3));

        assertDoesNotThrow(() -> service.desactive(accountIds, userId, transactionId));

        verify(obtainAccountGamePort).findByIdAndUserId(100L, userId, transactionId);
        verify(obtainAccountGamePort).findByIdAndUserId(200L, userId, transactionId);
        verify(obtainAccountGamePort).findByIdAndUserId(300L, userId, transactionId);
        verify(saveAccountGamePort, times(3)).save(any(AccountGameEntity.class), eq(transactionId));
    }

    @Test
    void testDesactive_accountNotFound_throwsException() {
        Long userId = 1L;
        String transactionId = "tx-desactive-002";
        List<Long> accountIds = List.of(100L);

        when(obtainAccountGamePort.findByIdAndUserId(100L, userId, transactionId))
                .thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.desactive(accountIds, userId, transactionId)
        );

        verify(obtainAccountGamePort).findByIdAndUserId(100L, userId, transactionId);
        verify(saveAccountGamePort, never()).save(any(AccountGameEntity.class), anyString());
    }

    @Test
    void testDesactive_partialFailure_throwsExceptionOnFirstNotFound() {
        Long userId = 1L;
        String transactionId = "tx-desactive-003";
        List<Long> accountIds = List.of(100L, 200L);

        AccountGameEntity account1 = new AccountGameEntity();
        account1.setId(100L);
        account1.setStatus(true);

        when(obtainAccountGamePort.findByIdAndUserId(100L, userId, transactionId))
                .thenReturn(Optional.of(account1));
        when(obtainAccountGamePort.findByIdAndUserId(200L, userId, transactionId))
                .thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.desactive(accountIds, userId, transactionId)
        );

        verify(obtainAccountGamePort).findByIdAndUserId(100L, userId, transactionId);
        verify(obtainAccountGamePort).findByIdAndUserId(200L, userId, transactionId);
        verify(saveAccountGamePort, times(1)).save(any(AccountGameEntity.class), eq(transactionId));
    }

    @Test
    void testDesactive_emptyList_doesNothing() {
        Long userId = 1L;
        String transactionId = "tx-desactive-004";
        List<Long> accountIds = Collections.emptyList();

        assertDoesNotThrow(() -> service.desactive(accountIds, userId, transactionId));

        verify(obtainAccountGamePort, never()).findByIdAndUserId(anyLong(), anyLong(), anyString());
        verify(saveAccountGamePort, never()).save(any(AccountGameEntity.class), anyString());
    }

}
