package com.register.wowlibre.repository;

import com.register.wowlibre.domain.enums.RealmServices;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.entities.RealmServicesEntity;
import com.register.wowlibre.infrastructure.repositories.server_services.JpaServerServicesAdapter;
import com.register.wowlibre.infrastructure.repositories.server_services.RealmServicesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaServerServicesAdapterTest {

    @Mock
    private RealmServicesRepository realmServicesRepository;

    private JpaServerServicesAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new JpaServerServicesAdapter(realmServicesRepository);
    }

    @Test
    void findByRealmId_shouldReturnList() {
        Long realmId = 1L;
        String transactionId = "tx-find-001";
        RealmServicesEntity entity1 = createRealmServicesEntity(1L, RealmServices.BANK, 1000.0, realmId);
        RealmServicesEntity entity2 = createRealmServicesEntity(2L, RealmServices.SEND_LEVEL, 500.0, realmId);
        List<RealmServicesEntity> expected = List.of(entity1, entity2);

        when(realmServicesRepository.findByRealmId_Id(realmId)).thenReturn(expected);

        List<RealmServicesEntity> result = adapter.findByRealmId(realmId, transactionId);

        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(realmServicesRepository).findByRealmId_Id(realmId);
    }

    @Test
    void findByRealmId_shouldReturnEmptyListWhenNoServices() {
        Long realmId = 1L;
        String transactionId = "tx-find-002";
        List<RealmServicesEntity> expected = List.of();

        when(realmServicesRepository.findByRealmId_Id(realmId)).thenReturn(expected);

        List<RealmServicesEntity> result = adapter.findByRealmId(realmId, transactionId);

        assertTrue(result.isEmpty());
        verify(realmServicesRepository).findByRealmId_Id(realmId);
    }

    @Test
    void findByNameAndRealmId_shouldReturnOptionalWhenFound() {
        RealmServices serviceName = RealmServices.BANK;
        Long realmId = 1L;
        String transactionId = "tx-find-name-001";
        RealmServicesEntity expectedEntity = createRealmServicesEntity(1L, serviceName, 1000.0, realmId);

        when(realmServicesRepository.findByNameAndRealmId_Id(serviceName, realmId))
                .thenReturn(Optional.of(expectedEntity));

        Optional<RealmServicesEntity> result = adapter.findByNameAndRealmId(serviceName, realmId, transactionId);

        assertTrue(result.isPresent());
        assertEquals(expectedEntity.getId(), result.get().getId());
        assertEquals(expectedEntity.getName(), result.get().getName());
        verify(realmServicesRepository).findByNameAndRealmId_Id(serviceName, realmId);
    }

    @Test
    void findByNameAndRealmId_shouldReturnEmptyWhenNotFound() {
        RealmServices serviceName = RealmServices.BANK;
        Long realmId = 1L;
        String transactionId = "tx-find-name-002";

        when(realmServicesRepository.findByNameAndRealmId_Id(serviceName, realmId))
                .thenReturn(Optional.empty());

        Optional<RealmServicesEntity> result = adapter.findByNameAndRealmId(serviceName, realmId, transactionId);

        assertFalse(result.isPresent());
        verify(realmServicesRepository).findByNameAndRealmId_Id(serviceName, realmId);
    }

    @Test
    void findByServersAvailableRequestLoa_shouldReturnList() {
        String transactionId = "tx-loa-001";
        RealmServicesEntity entity1 = createRealmServicesEntity(1L, RealmServices.BANK, 1000.0, 1L);
        RealmServicesEntity entity2 = createRealmServicesEntity(2L, RealmServices.BANK, 500.0, 2L);
        List<RealmServicesEntity> expected = List.of(entity1, entity2);

        when(realmServicesRepository.findActiveRealmServicesWithAmountGreaterThanZero(RealmServices.BANK))
                .thenReturn(expected);

        List<RealmServicesEntity> result = adapter.findByServersAvailableRequestLoa(transactionId);

        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(realmServicesRepository).findActiveRealmServicesWithAmountGreaterThanZero(RealmServices.BANK);
    }

    @Test
    void findByServersAvailableRequestLoa_shouldReturnEmptyListWhenNoServices() {
        String transactionId = "tx-loa-002";
        List<RealmServicesEntity> expected = List.of();

        when(realmServicesRepository.findActiveRealmServicesWithAmountGreaterThanZero(RealmServices.BANK))
                .thenReturn(expected);

        List<RealmServicesEntity> result = adapter.findByServersAvailableRequestLoa(transactionId);

        assertTrue(result.isEmpty());
        verify(realmServicesRepository).findActiveRealmServicesWithAmountGreaterThanZero(RealmServices.BANK);
    }

    @Test
    void findById_shouldReturnOptionalWhenFound() {
        Long id = 100L;
        RealmServicesEntity expectedEntity = createRealmServicesEntity(id, RealmServices.BANK, 1000.0, 1L);

        when(realmServicesRepository.findById(id)).thenReturn(Optional.of(expectedEntity));

        Optional<RealmServicesEntity> result = adapter.findById(id);

        assertTrue(result.isPresent());
        assertEquals(expectedEntity.getId(), result.get().getId());
        verify(realmServicesRepository).findById(id);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        Long id = 100L;

        when(realmServicesRepository.findById(id)).thenReturn(Optional.empty());

        Optional<RealmServicesEntity> result = adapter.findById(id);

        assertFalse(result.isPresent());
        verify(realmServicesRepository).findById(id);
    }

    @Test
    void save_shouldSaveEntity() {
        String transactionId = "tx-save-001";
        RealmServicesEntity entity = createRealmServicesEntity(1L, RealmServices.BANK, 1000.0, 1L);
        RealmServicesEntity savedEntity = createRealmServicesEntity(1L, RealmServices.BANK, 1000.0, 1L);

        when(realmServicesRepository.save(entity)).thenReturn(savedEntity);

        adapter.save(entity, transactionId);

        verify(realmServicesRepository).save(entity);
    }

    @Test
    void save_shouldHandleNullEntity() {
        String transactionId = "tx-save-002";
        RealmServicesEntity entity = null;

        adapter.save(entity, transactionId);

        verify(realmServicesRepository).save(entity);
    }

    private RealmServicesEntity createRealmServicesEntity(Long id, RealmServices name, Double amount, Long realmId) {
        RealmServicesEntity entity = new RealmServicesEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setAmount(amount);
        
        RealmEntity realmEntity = new RealmEntity();
        realmEntity.setId(realmId);
        entity.setRealmId(realmEntity);
        
        return entity;
    }
}

