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
        Long userId = 1L;

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
        assertEquals(1, response.getBody().getData().getRealms().size());
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
    void apiKey_found_returnsOk() {
        RealmModel dto = RealmModel.builder().apiSecret("secret").build();
        when(realmPort.findByApiKey("key", "tx")).thenReturn(dto);
        ResponseEntity<GenericResponse<String>> response = controller.apiKey("tx", "key");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("secret", response.getBody().getData());
        verify(realmPort).findByApiKey("key", "tx");
    }

    @Test
    void apiKey_notFound_returnsNoContent() {
        RealmModel dto = RealmModel.builder().build();
        when(realmPort.findByApiKey("key", "tx")).thenReturn(dto);
        ResponseEntity<GenericResponse<String>> response = controller.apiKey("tx", "key");

        assertEquals(204, response.getStatusCode().value());
        verify(realmPort).findByApiKey("key", "tx");
    }

    @Test
    void servers_returnsOkWithList() {
        List<RealmDto> servers = List.of(new RealmDto());
        when(realmPort.findByStatusIsTrue("tx")).thenReturn(servers);

        ResponseEntity<GenericResponse<List<RealmDto>>> response = controller.servers("tx");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        verify(realmPort).findByStatusIsTrue("tx");
    }

    @Test
    void vdpServer_returnsOk() {
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
        when(realmPort.findByServerNameAndExpansion("name", 1, Locale.ENGLISH, "tx")).thenReturn(server);

        ResponseEntity<GenericResponse<ServerVdpDto>> response =
                controller.vdpServer("tx", "name", 1, Locale.ENGLISH);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(server, response.getBody().getData());
        verify(realmPort).findByServerNameAndExpansion("name", 1, Locale.ENGLISH, "tx");
    }
}
