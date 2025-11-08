package com.register.wowlibre.service;

import com.register.wowlibre.application.services.account_validation.AccountValidationService;
import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.domain.port.out.account_game.ObtainAccountGamePort;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountValidationServiceTest extends BaseTest {

    @Mock
    private RealmPort realmPort;
    @Mock
    private ObtainAccountGamePort obtainAccountGamePort;

    private AccountValidationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AccountValidationService(realmPort, obtainAccountGamePort);
    }

    @Test
    void testVerifyAccount_success() {
        Long userId = 1L, realmId = 1L, accountId = 101L;
        String transactionId = "tx-001";

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
        assertEquals(realm.getId(), result.realm().getId());
        assertEquals(account.getId(), result.accountGame().getId());
        verify(realmPort).findById(realmId, transactionId);
        verify(obtainAccountGamePort).findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                transactionId);
    }

    @Test
    void testVerifyAccount_realmNotFound_throwsException() {
        Long userId = 1L, realmId = 1L, accountId = 101L;
        String transactionId = "tx-002";

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.verifyAccount(userId, accountId, realmId, transactionId)
        );

        verify(realmPort).findById(realmId, transactionId);
        verify(obtainAccountGamePort, never()).findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(anyLong(), anyLong(),
                anyLong(), anyString());
    }

    @Test
    void testVerifyAccount_accountGameNotFound_throwsException() {
        Long userId = 1L, realmId = 1L, accountId = 101L;
        String transactionId = "tx-003";

        RealmEntity realm = new RealmEntity();
        realm.setId(realmId);
        realm.setStatus(true);

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainAccountGamePort.findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                transactionId))
                .thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.verifyAccount(userId, accountId, realmId, transactionId)
        );

        verify(realmPort).findById(realmId, transactionId);
        verify(obtainAccountGamePort).findByUserIdAndAccountIdAndRealmIdAndStatusIsTrue(userId, accountId, realmId,
                transactionId);
    }

    private AccountGameEntity buildAccountGameEntity() {
        AccountGameEntity entity = new AccountGameEntity();
        entity.setId(101L);
        entity.setAccountId(200L);
        entity.setUsername("gameuser");
        entity.setStatus(true);
        return entity;
    }
}

