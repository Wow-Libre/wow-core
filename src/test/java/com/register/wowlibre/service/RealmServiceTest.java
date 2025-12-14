package com.register.wowlibre.service;

import com.register.wowlibre.application.services.I18nService;
import com.register.wowlibre.application.services.realm.RealmService;
import com.register.wowlibre.domain.dto.RealmCreateDto;
import com.register.wowlibre.domain.dto.RealmDto;
import com.register.wowlibre.domain.dto.ServerVdpDto;
import com.register.wowlibre.domain.dto.client.DashboardMetricsResponse;
import com.register.wowlibre.domain.enums.ResourceType;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.RealmModel;
import com.register.wowlibre.domain.port.in.integrator.IntegratorPort;
import com.register.wowlibre.domain.port.in.server_details.ObtainServerDetailsPort;
import com.register.wowlibre.domain.port.in.server_events.ServerEventsPort;
import com.register.wowlibre.domain.port.in.server_resources.ServerResourcesPort;
import com.register.wowlibre.domain.port.out.realm.ObtainRealmPort;
import com.register.wowlibre.domain.port.out.realm.SaveRealmPort;
import com.register.wowlibre.infrastructure.entities.RealmDetailsEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.entities.RealmEventsEntity;
import com.register.wowlibre.infrastructure.entities.RealmResourcesEntity;
import com.register.wowlibre.infrastructure.util.RandomString;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RealmServiceTest extends BaseTest {

    @Mock
    private ObtainRealmPort obtainRealmPort;
    @Mock
    private SaveRealmPort saveRealmPort;
    @Mock
    private RandomString randomString;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ObtainServerDetailsPort obtainServerDetailsPort;
    @Mock
    private IntegratorPort integratorPort;
    @Mock
    private ServerEventsPort serverEventsPort;
    @Mock
    private ServerResourcesPort serverResourcesPort;
    @Mock
    private I18nService i18nService;

    private RealmService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RealmService(obtainRealmPort, saveRealmPort, randomString, passwordEncoder,
                obtainServerDetailsPort, integratorPort, serverEventsPort, serverResourcesPort, i18nService);
    }

    @Test
    void findAll_shouldReturnListOfRealmDto() {
        String transactionId = "tx-realm-001";
        RealmEntity realm1 = createRealmEntity(1L, "Realm 1", true);
        RealmEntity realm2 = createRealmEntity(2L, "Realm 2", true);

        when(obtainRealmPort.findAll(transactionId)).thenReturn(List.of(realm1, realm2));

        List<RealmDto> result = service.findAll(transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Realm 1", result.get(0).getName());
        assertEquals("Realm 2", result.get(1).getName());
        verify(obtainRealmPort).findAll(transactionId);
    }

    @Test
    void findAll_shouldReturnEmptyList() {
        String transactionId = "tx-realm-002";

        when(obtainRealmPort.findAll(transactionId)).thenReturn(List.of());

        List<RealmDto> result = service.findAll(transactionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(obtainRealmPort).findAll(transactionId);
    }

    @Test
    void findByApiKey_shouldReturnRealmModel() {
        String apiKey = "api-key-123";
        String transactionId = "tx-realm-003";
        RealmEntity realm = createRealmEntity(1L, "Test Realm", true);

        when(obtainRealmPort.findByApiKey(apiKey, transactionId)).thenReturn(Optional.of(realm));

        RealmModel result = service.findByApiKey(apiKey, transactionId);

        assertNotNull(result);
        assertEquals(1L, result.id);
        assertEquals("Test Realm", result.name);
        verify(obtainRealmPort).findByApiKey(apiKey, transactionId);
    }

    @Test
    void findByApiKey_shouldReturnNullWhenNotFound() {
        String apiKey = "non-existent-key";
        String transactionId = "tx-realm-004";

        when(obtainRealmPort.findByApiKey(apiKey, transactionId)).thenReturn(Optional.empty());

        RealmModel result = service.findByApiKey(apiKey, transactionId);

        assertNull(result);
        verify(obtainRealmPort).findByApiKey(apiKey, transactionId);
    }

    @Test
    void findById_shouldReturnOptionalRealmEntity() {
        long id = 1L;
        String transactionId = "tx-realm-005";
        RealmEntity realm = createRealmEntity(id, "Test Realm", true);

        when(obtainRealmPort.findById(id, transactionId)).thenReturn(Optional.of(realm));

        Optional<RealmEntity> result = service.findById(id, transactionId);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(obtainRealmPort).findById(id, transactionId);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        long id = 999L;
        String transactionId = "tx-realm-006";

        when(obtainRealmPort.findById(id, transactionId)).thenReturn(Optional.empty());

        Optional<RealmEntity> result = service.findById(id, transactionId);

        assertFalse(result.isPresent());
        verify(obtainRealmPort).findById(id, transactionId);
    }

    @Test
    void create_shouldCreateRealmSuccessfully() {
        long userId = 1L;
        String transactionId = "tx-realm-007";
        String apiKey = "api-key-123";
        String apiSecret = "api-secret-456";
        String encodedPassword = "encoded-password";
        RealmCreateDto realmCreateDto = new RealmCreateDto();
        realmCreateDto.setName("New Realm");
        realmCreateDto.setEmulator("TrinityCore");
        realmCreateDto.setExpansion(2);
        realmCreateDto.setWebSite("https://test.com");
        realmCreateDto.setHost("http://test.com");
        realmCreateDto.setPassword("password123");
        realmCreateDto.setRealmlist("test.realmlist");
        realmCreateDto.setExternalUsername("admin");
        realmCreateDto.setExternalPassword("admin123");
        realmCreateDto.setType("PVP");

        when(obtainRealmPort.findByNameAndExpansion(realmCreateDto.getName(), realmCreateDto.getExpansion(), transactionId))
                .thenReturn(Optional.empty());
        when(randomString.nextString()).thenReturn(apiKey).thenReturn(apiSecret);
        when(passwordEncoder.encode(realmCreateDto.getPassword())).thenReturn(encodedPassword);

        ArgumentCaptor<RealmEntity> captor = ArgumentCaptor.forClass(RealmEntity.class);
        service.create(realmCreateDto, userId, transactionId);

        verify(obtainRealmPort).findByNameAndExpansion(realmCreateDto.getName(), realmCreateDto.getExpansion(), transactionId);
        verify(randomString, times(2)).nextString();
        verify(passwordEncoder).encode(realmCreateDto.getPassword());
        verify(saveRealmPort).save(captor.capture(), eq(transactionId));
        RealmEntity saved = captor.getValue();
        assertEquals("New Realm", saved.getName());
        assertEquals(apiKey, saved.getApiKey());
        assertEquals(apiSecret, saved.getApiSecret());
        assertFalse(saved.isStatus());
    }

    @Test
    void create_shouldThrowExceptionWhenRealmAlreadyExists() {
        long userId = 1L;
        String transactionId = "tx-realm-008";
        RealmCreateDto realmCreateDto = new RealmCreateDto();
        realmCreateDto.setName("Existing Realm");
        realmCreateDto.setExpansion(2);
        RealmEntity existing = createRealmEntity(1L, "Existing Realm", true);

        when(obtainRealmPort.findByNameAndExpansion(realmCreateDto.getName(), realmCreateDto.getExpansion(), transactionId))
                .thenReturn(Optional.of(existing));

        InternalException exception = assertThrows(InternalException.class, () ->
                service.create(realmCreateDto, userId, transactionId)
        );

        assertTrue(exception.getMessage().contains("It is not possible to create or configure a realm"));
        verify(obtainRealmPort).findByNameAndExpansion(realmCreateDto.getName(), realmCreateDto.getExpansion(), transactionId);
        verifyNoInteractions(randomString, passwordEncoder, saveRealmPort);
    }

    @Test
    void findByStatusIsTrueServers_shouldReturnList() {
        String transactionId = "tx-realm-009";
        RealmEntity realm1 = createRealmEntity(1L, "Realm 1", true);
        RealmEntity realm2 = createRealmEntity(2L, "Realm 2", true);

        when(obtainRealmPort.findByStatusIsTrue(transactionId)).thenReturn(List.of(realm1, realm2));

        List<RealmEntity> result = service.findByStatusIsTrueServers(transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(obtainRealmPort).findByStatusIsTrue(transactionId);
    }

    @Test
    void findByIdAndUserId_shouldReturnOptionalRealmEntity() {
        long id = 1L;
        long userId = 1L;
        String transactionId = "tx-realm-010";
        RealmEntity realm = createRealmEntity(id, "Test Realm", true);

        when(obtainRealmPort.findAndIdByUser(id, userId, transactionId)).thenReturn(Optional.of(realm));

        Optional<RealmEntity> result = service.findByIdAndUserId(id, userId, transactionId);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(obtainRealmPort).findAndIdByUser(id, userId, transactionId);
    }

    @Test
    void findByIdAndUserId_shouldReturnEmptyWhenNotFound() {
        long id = 999L;
        long userId = 1L;
        String transactionId = "tx-realm-011";

        when(obtainRealmPort.findAndIdByUser(id, userId, transactionId)).thenReturn(Optional.empty());

        Optional<RealmEntity> result = service.findByIdAndUserId(id, userId, transactionId);

        assertFalse(result.isPresent());
        verify(obtainRealmPort).findAndIdByUser(id, userId, transactionId);
    }

    @Test
    void findByServerNameAndExpansion_shouldReturnServerVdpDto() {
        Long id = 1L;
        String name = "Test Realm";
        Integer expansionId = 2;
        Locale locale = Locale.ENGLISH;
        String transactionId = "tx-realm-012";
        RealmEntity serverModel = createRealmEntity(id, name, true);
        RealmDetailsEntity detail1 = createRealmDetailsEntity(1L, serverModel, "key1", "value1");
        RealmResourcesEntity resource1 = createRealmResourcesEntity(1L, serverModel, ResourceType.LOGO, "logo.jpg");
        RealmEventsEntity event1 = createRealmEventsEntity(1L, serverModel, "Event 1");
        DashboardMetricsResponse dashboard = createDashboardMetricsResponse();

        when(obtainRealmPort.findById(id, transactionId))
                .thenReturn(Optional.of(serverModel));
        when(obtainServerDetailsPort.findByServerId(serverModel, transactionId))
                .thenReturn(List.of(detail1));
        when(serverResourcesPort.findByServerId(serverModel, transactionId))
                .thenReturn(List.of(resource1));
        when(integratorPort.dashboard(serverModel.getHost(), serverModel.getJwt(), transactionId))
                .thenReturn(dashboard);
        when(serverEventsPort.findByServerId(serverModel, transactionId))
                .thenReturn(List.of(event1));
        when(i18nService.tr("card-users-online", locale)).thenReturn("Users Online");
        when(i18nService.tr("card-total-users", locale)).thenReturn("Total Users");
        when(i18nService.tr("card-total-guild", locale)).thenReturn("Total Guilds");

        ServerVdpDto result = service.findByServerNameAndExpansion(id, expansionId, locale, transactionId);

        assertNotNull(result);
        assertEquals(name, result.name);
        assertEquals("logo.jpg", result.logo);
        assertEquals(3, result.cards.size());
        assertEquals(1, result.events.size());
        verify(obtainRealmPort).findById(id, transactionId);
        verify(obtainServerDetailsPort).findByServerId(serverModel, transactionId);
        verify(serverResourcesPort).findByServerId(serverModel, transactionId);
        verify(integratorPort).dashboard(serverModel.getHost(), serverModel.getJwt(), transactionId);
        verify(serverEventsPort).findByServerId(serverModel, transactionId);
    }

    @Test
    void findByServerNameAndExpansion_shouldThrowExceptionWhenRealmNotFound() {
        Long id = 999L;
        Integer expansionId = 2;
        Locale locale = Locale.ENGLISH;
        String transactionId = "tx-realm-013";

        when(obtainRealmPort.findById(id, transactionId))
                .thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.findByServerNameAndExpansion(id, expansionId, locale, transactionId)
        );

        assertNotNull(exception);
        verify(obtainRealmPort).findById(id, transactionId);
        verifyNoInteractions(obtainServerDetailsPort, serverResourcesPort, integratorPort, serverEventsPort);
    }

    @Test
    void findByStatusIsTrue_shouldReturnListOfRealmDto() {
        String transactionId = "tx-realm-014";
        RealmEntity realm1 = createRealmEntity(1L, "Realm 1", true);
        RealmEntity realm2 = createRealmEntity(2L, "Realm 2", true);

        when(obtainRealmPort.findByStatusIsTrue(transactionId)).thenReturn(List.of(realm1, realm2));

        List<RealmDto> result = service.findByStatusIsTrue(transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(obtainRealmPort).findByStatusIsTrue(transactionId);
    }

    @Test
    void findByNameAndVersionAndStatusIsTrue_shouldReturnRealmModel() {
        String name = "Test Realm";
        Integer expansionId = 2;
        String transactionId = "tx-realm-015";
        RealmEntity realm = createRealmEntity(1L, name, true);

        when(obtainRealmPort.findByNameAndExpansionAndStatusIsTrue(name, expansionId))
                .thenReturn(Optional.of(realm));

        RealmModel result = service.findByNameAndVersionAndStatusIsTrue(name, expansionId, transactionId);

        assertNotNull(result);
        assertEquals(1L, result.id);
        assertEquals(name, result.name);
        verify(obtainRealmPort).findByNameAndExpansionAndStatusIsTrue(name, expansionId);
    }

    @Test
    void findByNameAndVersionAndStatusIsTrue_shouldReturnNullWhenNotFound() {
        String name = "NonExistent Realm";
        Integer expansionId = 2;
        String transactionId = "tx-realm-016";

        when(obtainRealmPort.findByNameAndExpansionAndStatusIsTrue(name, expansionId))
                .thenReturn(Optional.empty());

        RealmModel result = service.findByNameAndVersionAndStatusIsTrue(name, expansionId, transactionId);

        assertNull(result);
        verify(obtainRealmPort).findByNameAndExpansionAndStatusIsTrue(name, expansionId);
    }

    private RealmEntity createRealmEntity(Long id, String name, boolean status) {
        RealmEntity realm = new RealmEntity();
        realm.setId(id);
        realm.setName(name);
        realm.setStatus(status);
        realm.setEmulator("TrinityCore");
        realm.setExpansionId(2);
        realm.setHost("http://test.com");
        realm.setJwt("jwt-token");
        realm.setApiKey("api-key-" + id);
        realm.setAvatarUrl("avatar.jpg");
        realm.setCreatedAt(LocalDateTime.now());
        realm.setWeb("https://test.com");
        realm.setRealmlist("test.realmlist");
        realm.setType("PVP");
        return realm;
    }

    private RealmDetailsEntity createRealmDetailsEntity(Long id, RealmEntity realm, String key, String value) {
        RealmDetailsEntity entity = new RealmDetailsEntity();
        entity.setId(id);
        entity.setRealmId(realm);
        entity.setKey(key);
        entity.setValue(value);
        return entity;
    }

    private RealmResourcesEntity createRealmResourcesEntity(Long id, RealmEntity realm, ResourceType resourceType, String url) {
        RealmResourcesEntity entity = new RealmResourcesEntity();
        entity.setId(id);
        entity.setRealmId(realm);
        entity.setResourceType(resourceType);
        entity.setUrl(url);
        return entity;
    }

    private RealmEventsEntity createRealmEventsEntity(Long id, RealmEntity realm, String title) {
        RealmEventsEntity entity = new RealmEventsEntity();
        entity.setId(id);
        entity.setRealmId(realm);
        entity.setTitle(title);
        entity.setDescription("Description");
        entity.setImg("img.jpg");
        entity.setDisclaimer("Disclaimer");
        return entity;
    }

    private DashboardMetricsResponse createDashboardMetricsResponse() {
        DashboardMetricsResponse response = new DashboardMetricsResponse();
        response.setOnlineUsers(100L);
        response.setTotalUsers(1000L);
        response.setTotalGuilds(50L);
        return response;
    }
}

