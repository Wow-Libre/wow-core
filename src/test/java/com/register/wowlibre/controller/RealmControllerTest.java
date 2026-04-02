package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.realm.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.controller.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealmControllerTest {
    private RealmPort realmPort;
    private RealmController controller;

    @BeforeEach
    void setUp() {
        realmPort = mock(RealmPort.class);
        controller = new RealmController(realmPort);
    }

    @Test
    void create_callsPortAndReturnsCreated() {
        RealmCreateDto dto = new RealmCreateDto();
        String tx = "tx";
        long userId = 1L;

        ResponseEntity<GenericResponse<Void>> response = controller.create(tx, userId, dto);

        assertEquals(201, response.getStatusCode().value());
        verify(realmPort).create(dto, userId, tx);
    }

    @Test
    void realms_returnsOkWithList() {
        List<RealmDto> realms = List.of(new RealmDto());
        when(realmPort.findAll("tx")).thenReturn(realms);

        ResponseEntity<GenericResponse<AssociatedRealm>> response = controller.realms("tx");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().getRealms().size());
        assertEquals(1, response.getBody().getData().getSize());
        verify(realmPort).findAll("tx");
    }

    @Test
    void realms_nullList_returnsNoContent() {
        when(realmPort.findAll("tx")).thenReturn(null);

        ResponseEntity<GenericResponse<AssociatedRealm>> response = controller.realms("tx");

        assertEquals(204, response.getStatusCode().value());
        verify(realmPort).findAll("tx");
    }

    @Test
    void servers_returnsOkWithList() {
        List<RealmDto> servers = List.of(new RealmDto());
        when(realmPort.findByStatusIsTrue("tx")).thenReturn(servers);

        ResponseEntity<GenericResponse<List<RealmDto>>> response = controller.servers("tx");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        verify(realmPort).findByStatusIsTrue("tx");
    }

    @Test
    void serversForGameRegistration_returnsOkWithList() {
        List<RealmDto> servers = List.of(new RealmDto());
        when(realmPort.findActiveForGameAccountRegistration("tx")).thenReturn(servers);

        ResponseEntity<GenericResponse<List<RealmDto>>> response =
                controller.serversForGameRegistration("tx");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        verify(realmPort).findActiveForGameAccountRegistration("tx");
    }

    @Test
    void vdp_returnsOk() {
        Long serverId = 1L;
        Integer expansionId = 1;
        Locale locale = Locale.ENGLISH;
        ServerVdpDto server = ServerVdpDto.builder()
                .realmlist("realm")
                .name("name")
                .type("type")
                .disclaimer("disclaimer")
                .information(new HashMap<>())
                .cards(new ArrayList<>())
                .events(new ArrayList<>())
                .url("url")
                .logo("logo")
                .headerLeftImg("left")
                .headerCenterImg("center")
                .headerRightImg("right")
                .youtubeUrl("youtube")
                .build();
        when(realmPort.findByServerNameAndExpansion(serverId, expansionId, locale, "tx")).thenReturn(server);

        ResponseEntity<GenericResponse<ServerVdpDto>> response =
                controller.vdp("tx", serverId, expansionId, locale);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(server, response.getBody().getData());
        verify(realmPort).findByServerNameAndExpansion(serverId, expansionId, locale, "tx");
    }

    @Test
    void inactive_callsPortAndReturnsOk() {
        String tx = "tx";
        long userId = 1L;
        long realmId = 10L;

        ResponseEntity<GenericResponse<Void>> response = controller.inactive(tx, userId, realmId);

        assertEquals(200, response.getStatusCode().value());
        verify(realmPort).delete(realmId, userId, tx);
    }

    @Test
    void getRealmListPing_returnsOkWithList() {
        String tx = "tx";
        String host = "http://localhost";
        List<RealmlistDto> realmlist = List.of(new RealmlistDto(1L, "Realm 1"));

        when(realmPort.getRealmLists(host, tx)).thenReturn(realmlist);

        ResponseEntity<GenericResponse<List<RealmlistDto>>> response =
                controller.getRealmListPing(tx, host);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(realmlist, response.getBody().getData());
        verify(realmPort).getRealmLists(host, tx);
    }
}
