package com.register.wowlibre.service;

import com.register.wowlibre.application.services.realm_services.RealmServicesService;
import com.register.wowlibre.domain.enums.RealmServices;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.RealmServicesModel;
import com.register.wowlibre.domain.port.out.realm_services.ObtainRealmServices;
import com.register.wowlibre.domain.port.out.realm_services.SaveRealmServices;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.entities.RealmServicesEntity;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealmServicesServiceTest extends BaseTest {

    @Mock
    private ObtainRealmServices obtainRealmServices;
    @Mock
    private SaveRealmServices saveRealmServices;

    private RealmServicesService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RealmServicesService(obtainRealmServices, saveRealmServices);
    }

    @Test
    void testFindByRealmId_shouldReturnListOfModels() {
        long realmId = 1L;
        String transactionId = "tx-001";

        RealmServicesEntity entity1 = buildRealmServicesEntity(1L, RealmServices.BANK, 1000.0, realmId);
        RealmServicesEntity entity2 = buildRealmServicesEntity(2L, RealmServices.SEND_LEVEL, 500.0, realmId);

        when(obtainRealmServices.findByRealmId(realmId, transactionId))
                .thenReturn(List.of(entity1, entity2));

        List<RealmServicesModel> result = service.findByRealmId(realmId, transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.getFirst().id());
        assertEquals(1000.0, result.getFirst().amount());
        assertEquals(realmId, result.getFirst().realmId());
        verify(obtainRealmServices).findByRealmId(realmId, transactionId);
    }

    @Test
    void testFindByRealmId_shouldReturnEmptyList() {
        long realmId = 1L;
        String transactionId = "tx-002";

        when(obtainRealmServices.findByRealmId(realmId, transactionId))
                .thenReturn(List.of());

        List<RealmServicesModel> result = service.findByRealmId(realmId, transactionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(obtainRealmServices).findByRealmId(realmId, transactionId);
    }

    @Test
    void testFindByNameAndRealmId_shouldReturnModel() {
        long realmId = 1L;
        String transactionId = "tx-003";
        RealmServices serviceName = RealmServices.BANK;

        RealmServicesEntity entity = buildRealmServicesEntity(1L, serviceName, 1000.0, realmId);

        when(obtainRealmServices.findByNameAndRealmId(serviceName, realmId, transactionId))
                .thenReturn(Optional.of(entity));

        RealmServicesModel result = service.findByNameAndRealmId(serviceName, realmId, transactionId);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1000.0, result.amount());
        assertEquals(realmId, result.realmId());
        verify(obtainRealmServices).findByNameAndRealmId(serviceName, realmId, transactionId);
    }

    @Test
    void testFindByNameAndRealmId_shouldReturnNullWhenNotFound() {
        long realmId = 1L;
        String transactionId = "tx-004";
        RealmServices serviceName = RealmServices.BANK;

        when(obtainRealmServices.findByNameAndRealmId(serviceName, realmId, transactionId))
                .thenReturn(Optional.empty());

        RealmServicesModel result = service.findByNameAndRealmId(serviceName, realmId, transactionId);

        assertNull(result);
        verify(obtainRealmServices).findByNameAndRealmId(serviceName, realmId, transactionId);
    }

    @Test
    void testFindByServersAvailableLoa_shouldReturnList() {
        String transactionId = "tx-005";

        RealmServicesEntity entity1 = buildRealmServicesEntity(1L, RealmServices.BANK, 1000.0, 1L);
        RealmServicesEntity entity2 = buildRealmServicesEntity(2L, RealmServices.BANK, 500.0, 2L);

        when(obtainRealmServices.findByServersAvailableRequestLoa(transactionId))
                .thenReturn(List.of(entity1, entity2));

        List<RealmServicesModel> result = service.findByServersAvailableLoa(transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(obtainRealmServices).findByServersAvailableRequestLoa(transactionId);
    }

    @Test
    void testFindByServersAvailableLoa_shouldReturnEmptyList() {
        String transactionId = "tx-006";

        when(obtainRealmServices.findByServersAvailableRequestLoa(transactionId))
                .thenReturn(List.of());

        List<RealmServicesModel> result = service.findByServersAvailableLoa(transactionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(obtainRealmServices).findByServersAvailableRequestLoa(transactionId);
    }

    @Test
    void testUpdateAmount_shouldUpdateSuccessfully() {
        long id = 1L;
        Double newAmount = 2000.0;
        String transactionId = "tx-007";

        RealmServicesEntity entity = buildRealmServicesEntity(id, RealmServices.BANK, 1000.0, 1L);

        when(obtainRealmServices.findById(id)).thenReturn(Optional.of(entity));

        assertDoesNotThrow(() -> service.updateAmount(id, newAmount, transactionId));

        assertEquals(newAmount, entity.getAmount());
        verify(obtainRealmServices).findById(id);
        verify(saveRealmServices).save(entity, transactionId);
    }

    @Test
    void testUpdateAmount_shouldThrowExceptionWhenNotFound() {
        long id = 1L;
        Double newAmount = 2000.0;
        String transactionId = "tx-008";

        when(obtainRealmServices.findById(id)).thenReturn(Optional.empty());

        assertThrows(InternalException.class, () ->
                service.updateAmount(id, newAmount, transactionId)
        );

        verify(obtainRealmServices).findById(id);
        verify(saveRealmServices, never()).save(any(), anyString());
    }

    @Test
    void testUpdateOrCreateAmountByServerId_shouldUpdateExisting() {
        long realmId = 1L;
        Double amount = 3000.0;
        String transactionId = "tx-009";
        RealmServices serviceName = RealmServices.BANK;

        RealmEntity realm = buildRealmEntity(realmId);
        RealmServicesEntity existingEntity = buildRealmServicesEntity(1L, serviceName, 1000.0, realmId);

        when(obtainRealmServices.findByNameAndRealmId(serviceName, realmId, transactionId))
                .thenReturn(Optional.of(existingEntity));

        assertDoesNotThrow(() -> service.updateOrCreateAmountByServerId(serviceName, realm, amount, transactionId));

        assertEquals(amount, existingEntity.getAmount());
        verify(obtainRealmServices).findByNameAndRealmId(serviceName, realmId, transactionId);
        verify(saveRealmServices).save(existingEntity, transactionId);
        verify(saveRealmServices, times(1)).save(any(), eq(transactionId));
    }

    @Test
    void testUpdateOrCreateAmountByServerId_shouldCreateNew() {
        long realmId = 1L;
        Double amount = 3000.0;
        String transactionId = "tx-010";
        RealmServices serviceName = RealmServices.BANK;

        RealmEntity realm = buildRealmEntity(realmId);

        when(obtainRealmServices.findByNameAndRealmId(serviceName, realmId, transactionId))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.updateOrCreateAmountByServerId(serviceName, realm, amount, transactionId));

        verify(obtainRealmServices).findByNameAndRealmId(serviceName, realmId, transactionId);
        verify(saveRealmServices).save(
                argThat(entity ->
                        entity.getRealmId().getId().equals(realmId) &&
                                entity.getName().equals(RealmServices.BANK) &&
                                entity.getAmount().equals(amount)
                ),
                eq(transactionId)
        );
    }

    @Test
    void testUpdateOrCreateAmountByServerId_shouldSetCorrectAmount() {
        long realmId = 1L;
        Double amount = 5000.0;
        String transactionId = "tx-011";
        RealmServices serviceName = RealmServices.BANK;

        RealmEntity realm = buildRealmEntity(realmId);
        RealmServicesEntity existingEntity = buildRealmServicesEntity(1L, serviceName, 1000.0, realmId);

        when(obtainRealmServices.findByNameAndRealmId(serviceName, realmId, transactionId))
                .thenReturn(Optional.of(existingEntity));

        service.updateOrCreateAmountByServerId(serviceName, realm, amount, transactionId);

        assertEquals(amount, existingEntity.getAmount());
        verify(saveRealmServices).save(existingEntity, transactionId);
    }

    private RealmServicesEntity buildRealmServicesEntity(Long id, RealmServices name, Double amount, Long realmId) {
        RealmServicesEntity entity = new RealmServicesEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setAmount(amount);
        entity.setRealmId(buildRealmEntity(realmId));
        return entity;
    }

    private RealmEntity buildRealmEntity(Long realmId) {
        RealmEntity realm = new RealmEntity();
        realm.setId(realmId);
        realm.setName("Test Realm");
        realm.setStatus(true);
        return realm;
    }
}

