package com.register.wowlibre.service;

import com.register.wowlibre.application.services.realm_advertising.RealmAdvertisingService;
import com.register.wowlibre.domain.dto.RealmAdvertisingDto;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.RealmAdvertisingModel;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.domain.port.out.realm_advertising.ObtainRealmAdvertising;
import com.register.wowlibre.domain.port.out.realm_advertising.SaveRealmAdvertising;
import com.register.wowlibre.infrastructure.entities.RealmAdvertisingEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealmAdvertisingServiceTest extends BaseTest {

    @Mock
    private SaveRealmAdvertising saveRealmAdvertising;
    @Mock
    private ObtainRealmAdvertising obtainRealmAdvertising;
    @Mock
    private RealmPort realmPort;

    private RealmAdvertisingService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RealmAdvertisingService(saveRealmAdvertising, obtainRealmAdvertising, realmPort);
    }

    @Test
    void getRealmAdvertisingById_shouldReturnModelWhenFound() {
        Long realmId = 1L;
        String language = "es";
        String transactionId = "tx-adv-001";
        RealmEntity realm = createRealmEntity(realmId);
        RealmAdvertisingEntity advertisingEntity = createRealmAdvertisingEntity(1L, realm, language);

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainRealmAdvertising.findByRealmId(realmId, language))
                .thenReturn(Optional.of(advertisingEntity));

        RealmAdvertisingModel result = service.getRealmAdvertisingById(realmId, language, transactionId);

        assertNotNull(result);
        assertEquals(1L, result.id);
        assertEquals("Test Tag", result.tag);
        assertEquals("Test Realm", result.title);
        verify(realmPort).findById(realmId, transactionId);
        verify(obtainRealmAdvertising).findByRealmId(realmId, language);
    }

    @Test
    void getRealmAdvertisingById_shouldReturnDefaultModelWhenNotFound() {
        Long realmId = 1L;
        String language = "es";
        String transactionId = "tx-adv-002";
        RealmEntity realm = createRealmEntity(realmId);

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainRealmAdvertising.findByRealmId(realmId, language))
                .thenReturn(Optional.empty());

        RealmAdvertisingModel result = service.getRealmAdvertisingById(realmId, language, transactionId);

        assertNotNull(result);
        assertEquals("Test Realm", result.title);
        assertNull(result.tag);
        assertFalse(result.copySuccess);
        verify(realmPort).findById(realmId, transactionId);
        verify(obtainRealmAdvertising).findByRealmId(realmId, language);
    }

    @Test
    void getRealmAdvertisingById_shouldThrowExceptionWhenRealmNotFound() {
        Long realmId = 999L;
        String language = "es";
        String transactionId = "tx-adv-003";

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.getRealmAdvertisingById(realmId, language, transactionId)
        );

        assertEquals("Realm not found", exception.getMessage());
        verify(realmPort).findById(realmId, transactionId);
        verifyNoInteractions(obtainRealmAdvertising);
    }

    @Test
    void save_shouldCreateNewRealmAdvertising() {
        Long realmId = 1L;
        String transactionId = "tx-adv-004";
        RealmEntity realm = createRealmEntity(realmId);
        RealmAdvertisingDto dto = new RealmAdvertisingDto();
        dto.setTag("New Tag");
        dto.setSubTitle("New SubTitle");
        dto.setDescription("New Description");
        dto.setCtaPrimary("New CTA");
        dto.setImgUrl("new-img.jpg");
        dto.setFooterDisclaimer("New Footer");
        dto.setLanguage("es");

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainRealmAdvertising.findByRealmId(realmId, dto.getLanguage()))
                .thenReturn(Optional.empty());

        ArgumentCaptor<RealmAdvertisingEntity> captor = ArgumentCaptor.forClass(RealmAdvertisingEntity.class);
        service.save(dto, realmId, transactionId);

        verify(realmPort).findById(realmId, transactionId);
        verify(obtainRealmAdvertising).findByRealmId(realmId, dto.getLanguage());
        verify(saveRealmAdvertising).save(captor.capture());
        RealmAdvertisingEntity saved = captor.getValue();
        assertEquals("New Tag", saved.getTag());
        assertEquals("New SubTitle", saved.getSubTitle());
        assertEquals(realm, saved.getRealmId());
    }

    @Test
    void save_shouldUpdateExistingRealmAdvertising() {
        Long realmId = 1L;
        String transactionId = "tx-adv-005";
        RealmEntity realm = createRealmEntity(realmId);
        RealmAdvertisingEntity existing = createRealmAdvertisingEntity(1L, realm, "es");
        RealmAdvertisingDto dto = new RealmAdvertisingDto();
        dto.setTag("Updated Tag");
        dto.setSubTitle("Updated SubTitle");
        dto.setDescription("Updated Description");
        dto.setCtaPrimary("Updated CTA");
        dto.setImgUrl("updated-img.jpg");
        dto.setFooterDisclaimer("Updated Footer");
        dto.setLanguage("es");

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.of(realm));
        when(obtainRealmAdvertising.findByRealmId(realmId, dto.getLanguage()))
                .thenReturn(Optional.of(existing));

        service.save(dto, realmId, transactionId);

        verify(realmPort).findById(realmId, transactionId);
        verify(obtainRealmAdvertising).findByRealmId(realmId, dto.getLanguage());
        verify(saveRealmAdvertising).save(existing);
        assertEquals("Updated Tag", existing.getTag());
        assertEquals("Updated SubTitle", existing.getSubTitle());
    }

    @Test
    void save_shouldThrowExceptionWhenRealmNotFound() {
        Long realmId = 999L;
        String transactionId = "tx-adv-006";
        RealmAdvertisingDto dto = new RealmAdvertisingDto();
        dto.setLanguage("es");

        when(realmPort.findById(realmId, transactionId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.save(dto, realmId, transactionId)
        );

        assertEquals("Realm not found", exception.getMessage());
        verify(realmPort).findById(realmId, transactionId);
        verifyNoInteractions(obtainRealmAdvertising, saveRealmAdvertising);
    }

    @Test
    void findByRealmsByLanguage_shouldReturnList() {
        String language = "es";
        String transactionId = "tx-adv-007";
        RealmEntity realm1 = createRealmEntity(1L);
        RealmEntity realm2 = createRealmEntity(2L);
        RealmAdvertisingEntity advertising1 = createRealmAdvertisingEntity(1L, realm1, language);
        RealmAdvertisingEntity advertising2 = createRealmAdvertisingEntity(2L, realm2, language);

        when(obtainRealmAdvertising.findByLanguage(language, transactionId))
                .thenReturn(List.of(advertising1, advertising2));

        List<RealmAdvertisingModel> result = service.findByRealmsByLanguage(language, transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id);
        assertEquals(2L, result.get(1).id);
        verify(obtainRealmAdvertising).findByLanguage(language, transactionId);
    }

    @Test
    void findByRealmsByLanguage_shouldReturnEmptyList() {
        String language = "es";
        String transactionId = "tx-adv-008";

        when(obtainRealmAdvertising.findByLanguage(language, transactionId))
                .thenReturn(List.of());

        List<RealmAdvertisingModel> result = service.findByRealmsByLanguage(language, transactionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(obtainRealmAdvertising).findByLanguage(language, transactionId);
    }

    private RealmEntity createRealmEntity(Long id) {
        RealmEntity realm = new RealmEntity();
        realm.setId(id);
        realm.setName("Test Realm");
        realm.setStatus(true);
        realm.setExpansionId(2);
        realm.setRealmlist("test.realmlist");
        return realm;
    }

    private RealmAdvertisingEntity createRealmAdvertisingEntity(Long id, RealmEntity realm, String language) {
        RealmAdvertisingEntity entity = new RealmAdvertisingEntity();
        entity.setId(id);
        entity.setRealmId(realm);
        entity.setTag("Test Tag");
        entity.setSubTitle("Test SubTitle");
        entity.setDescription("Test Description");
        entity.setCtaPrimary("Test CTA");
        entity.setImgUrl("test-img.jpg");
        entity.setFooterDisclaimer("Test Footer");
        entity.setLanguage(language);
        return entity;
    }
}

