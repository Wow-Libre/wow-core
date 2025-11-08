package com.register.wowlibre.service;

import com.register.wowlibre.application.services.teleport.TeleportService;
import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;
import com.register.wowlibre.domain.dto.teleport.TeleportDto;
import com.register.wowlibre.domain.enums.Faction;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.TeleportModel;
import com.register.wowlibre.domain.port.in.account_validation.AccountValidationPort;
import com.register.wowlibre.domain.port.in.integrator.IntegratorPort;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.domain.port.out.teleport.ObtainTeleport;
import com.register.wowlibre.domain.port.out.teleport.SaveTeleport;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.entities.TeleportEntity;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TeleportServiceTest extends BaseTest {

    @Mock
    private ObtainTeleport obtainTeleport;
    @Mock
    private SaveTeleport saveTeleport;
    @Mock
    private IntegratorPort integratorPort;
    @Mock
    private RealmPort realmPort;
    @Mock
    private AccountValidationPort accountValidationPort;

    private TeleportService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TeleportService(obtainTeleport, saveTeleport, integratorPort, realmPort, accountValidationPort);
    }

    @Test
    void findByAll_shouldReturnFilteredTeleportsByFaction() {
        Long realmId = 1L;
        Long raceId = 1L; // HUMAN -> ALLIANCE
        String transactionId = "tx-teleport-001";
        TeleportEntity teleport1 = createTeleportEntity(1L, realmId, Faction.ALLIANCE);
        TeleportEntity teleport2 = createTeleportEntity(2L, realmId, Faction.ALL);
        TeleportEntity teleport3 = createTeleportEntity(3L, realmId, Faction.HORDE);

        when(obtainTeleport.findAllTeleport(realmId)).thenReturn(List.of(teleport1, teleport2, teleport3));

        List<TeleportModel> result = service.findByAll(realmId, raceId, transactionId);

        assertNotNull(result);
        assertEquals(2, result.size()); // ALLIANCE and ALL
        verify(obtainTeleport).findAllTeleport(realmId);
    }

    @Test
    void findByAll_shouldReturnAllTeleportsWhenFactionIsAll() {
        Long realmId = 1L;
        Long raceId = 0L; // UNKNOWN -> ALL
        String transactionId = "tx-teleport-002";
        TeleportEntity teleport1 = createTeleportEntity(1L, realmId, Faction.ALLIANCE);
        TeleportEntity teleport2 = createTeleportEntity(2L, realmId, Faction.HORDE);
        TeleportEntity teleport3 = createTeleportEntity(3L, realmId, Faction.ALL);

        when(obtainTeleport.findAllTeleport(realmId)).thenReturn(List.of(teleport1, teleport2, teleport3));

        List<TeleportModel> result = service.findByAll(realmId, raceId, transactionId);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(obtainTeleport).findAllTeleport(realmId);
    }

    @Test
    void save_shouldSaveTeleportEntity() {
        String transactionId = "tx-teleport-003";
        TeleportDto teleportDto = new TeleportDto();
        teleportDto.setImgUrl("img.jpg");
        teleportDto.setName("Test Teleport");
        teleportDto.setPositionX(100.0);
        teleportDto.setPositionY(200.0);
        teleportDto.setPositionZ(300.0);
        teleportDto.setMap(0);
        teleportDto.setOrientation(1.5);
        teleportDto.setZone(1);
        teleportDto.setArea(10.0);
        teleportDto.setFaction("ALLIANCE");
        teleportDto.setRealmId(1L);

        RealmEntity realm = createRealmEntity(1L);
        when(realmPort.findById(teleportDto.getRealmId(), transactionId)).thenReturn(Optional.of(realm));
        when(obtainTeleport.findByNameAndRealmId(teleportDto.getName(), teleportDto.getRealmId()))
                .thenReturn(Optional.empty());

        ArgumentCaptor<TeleportEntity> captor = ArgumentCaptor.forClass(TeleportEntity.class);
        service.save(teleportDto, transactionId);

        verify(realmPort).findById(teleportDto.getRealmId(), transactionId);
        verify(obtainTeleport).findByNameAndRealmId(teleportDto.getName(), teleportDto.getRealmId());
        verify(saveTeleport).save(captor.capture());
        TeleportEntity saved = captor.getValue();
        assertEquals("Test Teleport", saved.getName());
        assertEquals(Faction.ALLIANCE, saved.getFaction());
    }

    @Test
    void save_shouldThrowExceptionWhenRealmNotFound() {
        String transactionId = "tx-teleport-004";
        TeleportDto teleportDto = new TeleportDto();
        teleportDto.setRealmId(999L);
        teleportDto.setName("Test Teleport");
        teleportDto.setFaction("ALLIANCE");

        when(realmPort.findById(teleportDto.getRealmId(), transactionId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.save(teleportDto, transactionId)
        );

        assertEquals("Server Invalid Or Not Found", exception.getMessage());
        verify(realmPort).findById(teleportDto.getRealmId(), transactionId);
        verifyNoInteractions(obtainTeleport, saveTeleport);
    }

    @Test
    void save_shouldThrowExceptionWhenTeleportAlreadyExists() {
        String transactionId = "tx-teleport-005";
        TeleportDto teleportDto = new TeleportDto();
        teleportDto.setRealmId(1L);
        teleportDto.setName("Existing Teleport");
        teleportDto.setFaction("ALLIANCE");

        RealmEntity realm = createRealmEntity(1L);
        TeleportEntity existing = createTeleportEntity(1L, 1L, Faction.ALLIANCE);
        existing.setName("Existing Teleport");

        when(realmPort.findById(teleportDto.getRealmId(), transactionId)).thenReturn(Optional.of(realm));
        when(obtainTeleport.findByNameAndRealmId(teleportDto.getName(), teleportDto.getRealmId()))
                .thenReturn(Optional.of(existing));

        InternalException exception = assertThrows(InternalException.class, () ->
                service.save(teleportDto, transactionId)
        );

        assertEquals("Teleport Already Exists", exception.getMessage());
        verify(realmPort).findById(teleportDto.getRealmId(), transactionId);
        verify(obtainTeleport).findByNameAndRealmId(teleportDto.getName(), teleportDto.getRealmId());
        verifyNoInteractions(saveTeleport);
    }

    @Test
    void teleport_shouldTeleportCharacter() {
        Long teleportId = 1L;
        Long userId = 1L;
        Long accountId = 101L;
        Long characterId = 201L;
        Long realmId = 1L;
        String transactionId = "tx-teleport-006";
        RealmEntity realm = createRealmEntity(realmId);
        realm.setHost("http://test.com");
        realm.setJwt("jwt-token");
        AccountVerificationDto verificationDto = new AccountVerificationDto(realm, null);
        TeleportEntity teleport = createTeleportEntity(teleportId, realmId, Faction.ALLIANCE);

        when(accountValidationPort.verifyAccount(userId, accountId, realmId, transactionId))
                .thenReturn(verificationDto);
        when(obtainTeleport.findById(teleportId)).thenReturn(Optional.of(teleport));

        service.teleport(teleportId, userId, accountId, characterId, realmId, transactionId);

        verify(accountValidationPort).verifyAccount(userId, accountId, realmId, transactionId);
        verify(obtainTeleport).findById(teleportId);
        verify(integratorPort).teleport(eq(realm.getHost()), eq(realm.getJwt()), any(), eq(transactionId));
    }

    @Test
    void delete_shouldDeleteTeleport() {
        Long id = 1L;
        Long realmId = 1L;
        String transactionId = "tx-teleport-007";
        TeleportEntity teleport = createTeleportEntity(id, realmId, Faction.ALLIANCE);

        when(obtainTeleport.findByIdAndRealmId(id, realmId)).thenReturn(Optional.of(teleport));

        service.delete(id, realmId, transactionId);

        verify(obtainTeleport).findByIdAndRealmId(id, realmId);
        verify(saveTeleport).delete(teleport);
    }

    @Test
    void delete_shouldThrowExceptionWhenTeleportNotFound() {
        Long id = 999L;
        Long realmId = 1L;
        String transactionId = "tx-teleport-008";

        when(obtainTeleport.findByIdAndRealmId(id, realmId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.delete(id, realmId, transactionId)
        );

        assertEquals("Teleport Not Found", exception.getMessage());
        verify(obtainTeleport).findByIdAndRealmId(id, realmId);
        verifyNoInteractions(saveTeleport);
    }

    private TeleportEntity createTeleportEntity(Long id, Long realmId, Faction faction) {
        TeleportEntity entity = new TeleportEntity();
        entity.setId(id);
        entity.setName("Teleport " + id);
        entity.setImgUrl("img.jpg");
        entity.setPositionX(100.0);
        entity.setPositionY(200.0);
        entity.setPositionZ(300.0);
        entity.setMap(0);
        entity.setOrientation(1.5);
        entity.setZone(1);
        entity.setArea(10.0);
        entity.setFaction(faction);
        RealmEntity realm = createRealmEntity(realmId);
        entity.setRealmId(realm);
        return entity;
    }

    private RealmEntity createRealmEntity(Long id) {
        RealmEntity realm = new RealmEntity();
        realm.setId(id);
        realm.setName("Test Realm");
        realm.setStatus(true);
        realm.setExpansionId(2);
        realm.setHost("http://test.com");
        realm.setJwt("jwt-token");
        realm.setRealmlist("test.realmlist");
        return realm;
    }
}

