package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.teleport.CharacterTeleportDto;
import com.register.wowlibre.domain.dto.teleport.TeleportDto;
import com.register.wowlibre.domain.model.TeleportModel;
import com.register.wowlibre.domain.port.in.teleport.TeleportPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.TeleportController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TeleportControllerTest {

    @Mock
    private TeleportPort teleportPort;

    @InjectMocks
    private TeleportController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllTeleports() {
        String transactionId = "tx-teleport-001";
        Long raceId = 1L;
        Long realmId = 1L;
        TeleportModel teleport = TeleportModel.builder()
                .id(1L)
                .name("Test Teleport")
                .realmId(realmId)
                .build();
        List<TeleportModel> teleports = List.of(teleport);

        when(teleportPort.findByAll(realmId, raceId, transactionId)).thenReturn(teleports);

        ResponseEntity<GenericResponse<List<TeleportModel>>> response = controller.all(transactionId, raceId, realmId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        verify(teleportPort).findByAll(realmId, raceId, transactionId);
    }

    @Test
    void shouldTeleportCharacter() {
        String transactionId = "tx-teleport-002";
        Long userId = 1L;
        CharacterTeleportDto teleportDto = new CharacterTeleportDto();
        teleportDto.setTeleportId(1L);
        teleportDto.setAccountId(101L);
        teleportDto.setCharacterId(201L);
        teleportDto.setRealmId(1L);

        ResponseEntity<GenericResponse<Void>> response = controller.character(transactionId, userId, teleportDto);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(teleportPort).teleport(teleportDto.getTeleportId(), userId, teleportDto.getAccountId(),
                teleportDto.getCharacterId(), teleportDto.getRealmId(), transactionId);
    }

    @Test
    void shouldCreateTeleport() {
        String transactionId = "tx-teleport-003";
        TeleportDto teleportDto = new TeleportDto();

        ResponseEntity<GenericResponse<Void>> response = controller.create(transactionId, teleportDto);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(teleportPort).save(teleportDto, transactionId);
    }

    @Test
    void shouldDeleteTeleport() {
        String transactionId = "tx-teleport-004";
        Long realmId = 1L;
        Long teleportId = 1L;

        ResponseEntity<GenericResponse<Void>> response = controller.delete(transactionId, realmId, teleportId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(teleportPort).delete(teleportId, realmId, transactionId);
    }
}

