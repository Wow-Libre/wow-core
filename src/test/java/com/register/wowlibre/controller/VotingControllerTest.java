package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.voting.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.voting_platforms.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.controller.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VotingControllerTest {
    private VotingPlatformsPort votingPlatformsPort;
    private VotingController controller;

    @BeforeEach
    void setUp() {
        votingPlatformsPort = mock(VotingPlatformsPort.class);
        controller = new VotingController(votingPlatformsPort);
    }

    @Test
    void getAllActiveVotingPlatforms_returnsOkWithList() {
        VotingPlatformsModel model = new VotingPlatformsModel(1L, "", "", "");
        when(votingPlatformsPort.findAllActiveVotingPlatforms(null, "tx")).thenReturn(List.of(model));

        ResponseEntity<GenericResponse<List<VotingPlatformsModel>>> response =
                controller.getAllActiveVotingPlatforms("tx");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        verify(votingPlatformsPort).findAllActiveVotingPlatforms(null, "tx");
    }

    @Test
    void getAllActiveVotingPlatformsLogged_returnsOkWithList() {
        VotingPlatformsModel model = new VotingPlatformsModel(1L, "", "", "");
        when(votingPlatformsPort.findAllActiveVotingPlatforms(5L, "tx")).thenReturn(List.of(model));

        ResponseEntity<GenericResponse<List<VotingPlatformsModel>>> response =
                controller.getAllActiveVotingPlatformsLogged("tx", 5L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        verify(votingPlatformsPort).findAllActiveVotingPlatforms(5L, "tx");
    }

    @Test
    void createVotingPlatform_callsPortAndReturnsOk() {
        VotingPlatformDto dto = new VotingPlatformDto();
        dto.setName("test");
        dto.setImgUrl("img");
        dto.setPostbackUrl("postback");
        dto.setAllowedHost("host");

        ResponseEntity<GenericResponse<Void>> response =
                controller.createVotingPlatform("tx", dto);

        assertEquals(200, response.getStatusCode().value());
        verify(votingPlatformsPort).createVotingPlatform("test", "img", "postback", "host", "tx");
    }

    @Test
    void updateVotingPlatform_callsPortAndReturnsOk() {
        VotingPlatformDto dto = new VotingPlatformDto();
        dto.setName("test");
        dto.setImgUrl("img");
        dto.setPostbackUrl("postback");
        dto.setAllowedHost("host");

        ResponseEntity<GenericResponse<Void>> response =
                controller.updateVotingPlatform("tx", 1L, dto);

        assertEquals(200, response.getStatusCode().value());
        verify(votingPlatformsPort).updateVotingPlatform(1L, "test", "img", "postback", "host", "tx");
    }

    @Test
    void deleteVotingPlatform_callsPortAndReturnsOk() {
        ResponseEntity<GenericResponse<Void>> response =
                controller.deleteVotingPlatform("tx", 2L);

        assertEquals(200, response.getStatusCode().value());
        verify(votingPlatformsPort).deleteVotingPlatform(2L, "tx");
    }

    @Test
    void postback_callsPortAndReturnsOk() {
        ResponseEntity<GenericResponse<Void>> response =
                controller.postback("tx", "param", "ip");

        assertEquals(200, response.getStatusCode().value());
        verify(votingPlatformsPort).postbackVotingPlatform("param", "127.0.0.1", "tx");
    }

    @Test
    void wallet_callsPortAndReturnsOk() {
        when(votingPlatformsPort.votes(7L, "tx")).thenReturn(10);

        ResponseEntity<GenericResponse<Integer>> response =
                controller.wallet("tx", 7L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getData());
        verify(votingPlatformsPort).votes(7L, "tx");
    }
}
