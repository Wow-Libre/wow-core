package com.register.wowlibre.service;

import com.register.wowlibre.application.services.bank.BankService;
import com.register.wowlibre.domain.dto.RealmAvailableBankDto;
import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;
import com.register.wowlibre.domain.enums.RealmServices;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.RealmServicesModel;
import com.register.wowlibre.domain.model.resources.PlanModel;
import com.register.wowlibre.domain.port.in.ResourcesPort;
import com.register.wowlibre.domain.port.in.account_validation.AccountValidationPort;
import com.register.wowlibre.domain.port.in.realm_services.RealmServicesPort;
import com.register.wowlibre.domain.port.out.credit_loans.ObtainCreditLoans;
import com.register.wowlibre.domain.port.out.credit_loans.SaveCreditLoans;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.CreditLoansEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.util.RandomString;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BankServiceTest extends BaseTest {

    @Mock
    private ObtainCreditLoans obtainCreditLoans;
    @Mock
    private SaveCreditLoans saveCreditLoans;
    @Mock
    private RealmServicesPort realmServicesPort;
    @Mock
    private ResourcesPort resourcesPort;
    @Mock
    private AccountValidationPort accountValidationPort;
    @Mock
    private RandomString randomString;

    private BankService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new BankService(obtainCreditLoans, saveCreditLoans, realmServicesPort,
                resourcesPort, accountValidationPort, randomString);
    }

    @Test
    void applyForLoan_success_createsLoan() {
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        Long realmId = 1L;
        Long planId = 1L;
        String transactionId = "tx-loan-001";
        String referenceSerial = "REF-123";

        RealmEntity realm = createRealmEntity(realmId, true);
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        RealmServicesModel realmServicesModel = new RealmServicesModel(1L, "BANK", 1000.0, realmId, "Test Realm");
        PlanModel plan = new PlanModel(planId, "Plan 1", "Description", "100", "monthly",
                List.of("feature1"), "button", 10, 3, 500.0);

        when(accountValidationPort.verifyAccount(userId, accountId, realmId, transactionId))
                .thenReturn(verificationDto);
        when(realmServicesPort.findByNameAndRealmId(RealmServices.BANK, realmId, transactionId))
                .thenReturn(realmServicesModel);
        when(resourcesPort.getPlansBank("es", transactionId)).thenReturn(List.of(plan));
        when(obtainCreditLoans.findByAccountGameAndStatusIsTrue(accountGame))
                .thenReturn(Collections.emptyList());
        when(randomString.nextString()).thenReturn(referenceSerial);

        assertDoesNotThrow(() -> service.applyForLoan(userId, accountId, characterId, realmId, planId, transactionId));

        verify(accountValidationPort).verifyAccount(userId, accountId, realmId, transactionId);
        verify(realmServicesPort).findByNameAndRealmId(RealmServices.BANK, realmId, transactionId);
        verify(resourcesPort).getPlansBank("es", transactionId);
        verify(obtainCreditLoans).findByAccountGameAndStatusIsTrue(accountGame);
        verify(realmServicesPort).updateAmount(realmServicesModel.id(), realmServicesModel.amount() - 1, transactionId);
        verify(saveCreditLoans).save(any(CreditLoansEntity.class), eq(transactionId));
        verify(randomString).nextString();
    }

    @Test
    void applyForLoan_realmNotAvailable_throwsException() {
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        Long realmId = 1L;
        Long planId = 1L;
        String transactionId = "tx-loan-002";

        RealmEntity realm = createRealmEntity(realmId, false);
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        when(accountValidationPort.verifyAccount(userId, accountId, realmId, transactionId))
                .thenReturn(verificationDto);

        InternalException exception = assertThrows(InternalException.class, () ->
                service.applyForLoan(userId, accountId, characterId, realmId, planId, transactionId)
        );

        assertEquals("Currently the realm is not available to accept loans, contact the administrator.", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, realmId, transactionId);
        verifyNoInteractions(realmServicesPort, resourcesPort, obtainCreditLoans, saveCreditLoans);
    }

    @Test
    void applyForLoan_realmServicesNotConfigured_throwsException() {
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        Long realmId = 1L;
        Long planId = 1L;
        String transactionId = "tx-loan-003";

        RealmEntity realm = createRealmEntity(realmId, true);
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        when(accountValidationPort.verifyAccount(userId, accountId, realmId, transactionId))
                .thenReturn(verificationDto);
        when(realmServicesPort.findByNameAndRealmId(RealmServices.BANK, realmId, transactionId))
                .thenReturn(null);

        InternalException exception = assertThrows(InternalException.class, () ->
                service.applyForLoan(userId, accountId, characterId, realmId, planId, transactionId)
        );

        assertEquals("The realm currently does not have loans configured", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, realmId, transactionId);
        verify(realmServicesPort).findByNameAndRealmId(RealmServices.BANK, realmId, transactionId);
        verifyNoInteractions(resourcesPort, obtainCreditLoans, saveCreditLoans);
    }

    @Test
    void applyForLoan_noMoneyAvailable_throwsException() {
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        Long realmId = 1L;
        Long planId = 1L;
        String transactionId = "tx-loan-004";

        RealmEntity realm = createRealmEntity(realmId, true);
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        RealmServicesModel realmServicesModel = new RealmServicesModel(1L, "BANK", 0.0, realmId, "Test Realm");

        when(accountValidationPort.verifyAccount(userId, accountId, realmId, transactionId))
                .thenReturn(verificationDto);
        when(realmServicesPort.findByNameAndRealmId(RealmServices.BANK, realmId, transactionId))
                .thenReturn(realmServicesModel);

        InternalException exception = assertThrows(InternalException.class, () ->
                service.applyForLoan(userId, accountId, characterId, realmId, planId, transactionId)
        );

        assertEquals("There is no money available for loans", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, realmId, transactionId);
        verify(realmServicesPort).findByNameAndRealmId(RealmServices.BANK, realmId, transactionId);
        verifyNoInteractions(resourcesPort, obtainCreditLoans, saveCreditLoans);
    }

    @Test
    void applyForLoan_planNotFound_throwsException() {
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        Long realmId = 1L;
        Long planId = 999L;
        String transactionId = "tx-loan-005";

        RealmEntity realm = createRealmEntity(realmId, true);
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        RealmServicesModel realmServicesModel = new RealmServicesModel(1L, "BANK", 1000.0, realmId, "Test Realm");
        PlanModel plan = new PlanModel(1L, "Plan 1", "Description", "100", "monthly",
                List.of("feature1"), "button", 10, 3, 500.0);

        when(accountValidationPort.verifyAccount(userId, accountId, realmId, transactionId))
                .thenReturn(verificationDto);
        when(realmServicesPort.findByNameAndRealmId(RealmServices.BANK, realmId, transactionId))
                .thenReturn(realmServicesModel);
        when(resourcesPort.getPlansBank("es", transactionId)).thenReturn(List.of(plan));

        InternalException exception = assertThrows(InternalException.class, () ->
                service.applyForLoan(userId, accountId, characterId, realmId, planId, transactionId)
        );

        assertEquals("The requested plan is not available.", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, realmId, transactionId);
        verify(realmServicesPort).findByNameAndRealmId(RealmServices.BANK, realmId, transactionId);
        verify(resourcesPort).getPlansBank("es", transactionId);
        verifyNoInteractions(obtainCreditLoans, saveCreditLoans);
    }

    @Test
    void applyForLoan_userAlreadyHasActiveLoan_throwsException() {
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        Long realmId = 1L;
        Long planId = 1L;
        String transactionId = "tx-loan-006";

        RealmEntity realm = createRealmEntity(realmId, true);
        AccountGameEntity accountGame = createAccountGameEntity(accountId);
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, accountGame);

        RealmServicesModel realmServicesModel = new RealmServicesModel(1L, "BANK", 1000.0, realmId, "Test Realm");
        PlanModel plan = new PlanModel(planId, "Plan 1", "Description", "100", "monthly",
                List.of("feature1"), "button", 10, 3, 500.0);
        CreditLoansEntity existingLoan = new CreditLoansEntity();

        when(accountValidationPort.verifyAccount(userId, accountId, realmId, transactionId))
                .thenReturn(verificationDto);
        when(realmServicesPort.findByNameAndRealmId(RealmServices.BANK, realmId, transactionId))
                .thenReturn(realmServicesModel);
        when(resourcesPort.getPlansBank("es", transactionId)).thenReturn(List.of(plan));
        when(obtainCreditLoans.findByAccountGameAndStatusIsTrue(accountGame))
                .thenReturn(List.of(existingLoan));

        InternalException exception = assertThrows(InternalException.class, () ->
                service.applyForLoan(userId, accountId, characterId, realmId, planId, transactionId)
        );

        assertEquals("You already have a loan.", exception.getMessage());
        verify(accountValidationPort).verifyAccount(userId, accountId, realmId, transactionId);
        verify(realmServicesPort).findByNameAndRealmId(RealmServices.BANK, realmId, transactionId);
        verify(resourcesPort).getPlansBank("es", transactionId);
        verify(obtainCreditLoans).findByAccountGameAndStatusIsTrue(accountGame);
        verifyNoInteractions(saveCreditLoans);
    }

    @Test
    void getAvailableLoansByRealm_success_returnsList() {
        String transactionId = "tx-available-001";
        RealmServicesModel service1 = new RealmServicesModel(1L, "BANK", 1000.0, 1L, "Realm 1");
        RealmServicesModel service2 = new RealmServicesModel(2L, "BANK", 500.0, 2L, "Realm 2");

        when(realmServicesPort.findByServersAvailableLoa(transactionId))
                .thenReturn(List.of(service1, service2));

        List<RealmAvailableBankDto> result = service.getAvailableLoansByRealm(transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Realm 1", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Realm 2", result.get(1).getName());
        verify(realmServicesPort).findByServersAvailableLoa(transactionId);
    }

    @Test
    void getAvailableLoansByRealm_emptyList_returnsEmptyList() {
        String transactionId = "tx-available-002";

        when(realmServicesPort.findByServersAvailableLoa(transactionId))
                .thenReturn(Collections.emptyList());

        List<RealmAvailableBankDto> result = service.getAvailableLoansByRealm(transactionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(realmServicesPort).findByServersAvailableLoa(transactionId);
    }

    private RealmEntity createRealmEntity(Long id, boolean status) {
        RealmEntity realm = new RealmEntity();
        realm.setId(id);
        realm.setStatus(status);
        realm.setName("Test Realm");
        return realm;
    }

    private AccountGameEntity createAccountGameEntity(Long accountId) {
        AccountGameEntity accountGame = new AccountGameEntity();
        accountGame.setId(accountId);
        accountGame.setAccountId(accountId);
        accountGame.setStatus(true);
        return accountGame;
    }
}

