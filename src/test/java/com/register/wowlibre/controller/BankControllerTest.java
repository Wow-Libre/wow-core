package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.BankRequestDto;
import com.register.wowlibre.domain.dto.RealmAvailableBankDto;
import com.register.wowlibre.domain.port.in.bank.BankPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.BankController;
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

class BankControllerTest {

    @Mock
    private BankPort bankPort;

    @InjectMocks
    private BankController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRequestLoan() {
        // Arrange
        Long userId = 1L;
        String transactionId = "tx-bank-001";
        BankRequestDto requestDto = new BankRequestDto();
        requestDto.setAccountId(101L);
        requestDto.setCharacterId(201L);
        requestDto.setServerId(1L);
        requestDto.setPlanId(1L);

        // Act
        ResponseEntity<GenericResponse<Void>> response = controller.bank(
                transactionId, userId, requestDto
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(bankPort).applyForLoan(userId, requestDto.getAccountId(), requestDto.getCharacterId(),
                requestDto.getServerId(), requestDto.getPlanId(), transactionId);
    }

    @Test
    void shouldRequestLoanWithNullTransactionId() {
        // Arrange
        Long userId = 1L;
        String transactionId = null;
        BankRequestDto requestDto = new BankRequestDto();
        requestDto.setAccountId(101L);
        requestDto.setCharacterId(201L);
        requestDto.setServerId(1L);
        requestDto.setPlanId(1L);

        // Act
        ResponseEntity<GenericResponse<Void>> response = controller.bank(
                transactionId, userId, requestDto
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(bankPort).applyForLoan(userId, requestDto.getAccountId(), requestDto.getCharacterId(),
                requestDto.getServerId(), requestDto.getPlanId(), transactionId);
    }

    @Test
    void shouldReturnAvailableLoansByRealm() {
        // Arrange
        String transactionId = "tx-available-001";
        RealmAvailableBankDto server1 = new RealmAvailableBankDto(1L, "Realm 1");
        RealmAvailableBankDto server2 = new RealmAvailableBankDto(2L, "Realm 2");
        List<RealmAvailableBankDto> servers = List.of(server1, server2);

        when(bankPort.getAvailableLoansByRealm(transactionId)).thenReturn(servers);

        // Act
        ResponseEntity<GenericResponse<List<RealmAvailableBankDto>>> response = controller.availableLoansByRealm(
                transactionId
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(2);
        assertThat(response.getBody().getData().get(0).getId()).isEqualTo(1L);
        assertThat(response.getBody().getData().get(0).getName()).isEqualTo("Realm 1");
        assertThat(response.getBody().getData().get(1).getId()).isEqualTo(2L);
        assertThat(response.getBody().getData().get(1).getName()).isEqualTo("Realm 2");
        verify(bankPort).getAvailableLoansByRealm(transactionId);
    }

    @Test
    void shouldReturnEmptyListWhenNoAvailableServers() {
        // Arrange
        String transactionId = "tx-available-002";
        List<RealmAvailableBankDto> servers = List.of();

        when(bankPort.getAvailableLoansByRealm(transactionId)).thenReturn(servers);

        // Act
        ResponseEntity<GenericResponse<List<RealmAvailableBankDto>>> response = controller.availableLoansByRealm(
                transactionId
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEmpty();
        verify(bankPort).getAvailableLoansByRealm(transactionId);
    }

    @Test
    void shouldReturnAvailableLoansByRealmWithNullTransactionId() {
        // Arrange
        String transactionId = null;
        RealmAvailableBankDto server1 = new RealmAvailableBankDto(1L, "Realm 1");
        List<RealmAvailableBankDto> servers = List.of(server1);

        when(bankPort.getAvailableLoansByRealm(transactionId)).thenReturn(servers);

        // Act
        ResponseEntity<GenericResponse<List<RealmAvailableBankDto>>> response = controller.availableLoansByRealm(
                transactionId
        );

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        verify(bankPort).getAvailableLoansByRealm(transactionId);
    }
}

