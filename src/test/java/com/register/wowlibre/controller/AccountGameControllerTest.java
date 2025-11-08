package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.account_game.AccountGameDetailDto;
import com.register.wowlibre.domain.dto.account_game.AccountsGameDto;
import com.register.wowlibre.domain.dto.account_game.CreateAccountGameDto;
import com.register.wowlibre.domain.dto.client.AccountBannedResponse;
import com.register.wowlibre.domain.model.AccountGameModel;
import com.register.wowlibre.domain.port.in.account_game.AccountGamePort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.AccountGameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountGameControllerTest {

    @Mock
    private AccountGamePort accountGamePort;

    @InjectMocks
    private AccountGameController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAvailableAccounts() {
        // Arrange
        long userId = 1L;
        String transactionId = "tx123";
        int page = 0;
        int size = 5;
        String username = "testUser";
        String server = "Azeroth";

        List<AccountGameModel> accounts = List.of(
                new AccountGameModel(1L, username, 100L, "test@mail.com", server, 10L,
                        "WOTLK", 2, "avatar.png", "web", true, "realmlist")
        );
        AccountsGameDto dto = new AccountsGameDto(accounts, 1L);

        when(accountGamePort.accounts(userId, page, size, username, server, transactionId)).thenReturn(dto);

        // Act
        ResponseEntity<GenericResponse<AccountsGameDto>> response = controller.accounts(
                transactionId, userId, username, server, page, size
        );

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getAccounts()).hasSize(1);
        verify(accountGamePort).accounts(userId, page, size, username, server, transactionId);
    }

    @Test
    void shouldCreateAccount() {
        // Arrange
        long userId = 1L;
        String transactionId = "tx123";
        CreateAccountGameDto createDto = new CreateAccountGameDto();
        createDto.setExpansion(2);
        createDto.setUsername("newUser");
        createDto.setPassword("pass123");
        createDto.setRealmName("Azeroth");
        // Act
        ResponseEntity<GenericResponse<Void>> response = controller.create(transactionId, userId, createDto);

        // Assert
        assertEquals(201, response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(accountGamePort).create(userId, "Azeroth", 2, "newUser", null, "pass123", transactionId);
    }

    @Test
    void shouldReturnAccountsByRealm() {
        // Arrange
        long userId = 1L;
        String transactionId = "tx123";
        long serverId = 10L;

        List<AccountGameModel> accounts = List.of(
                new AccountGameModel(1L, "user1", 100L, "test@mail.com", "Azeroth", serverId,
                        "WOTLK", 2, "avatar.png", "web", true, "realmlist")
        );
        AccountsGameDto dto = new AccountsGameDto(accounts, 1L);

        when(accountGamePort.accounts(userId, serverId, transactionId)).thenReturn(dto);

        // Act
        ResponseEntity<GenericResponse<AccountsGameDto>> response = controller.accountsByRealm(
                transactionId, userId, serverId
        );

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getAccounts()).hasSize(1);
        verify(accountGamePort).accounts(userId, serverId, transactionId);
    }

    @Test
    void shouldReturnAccountDetails() {
        // Arrange
        long userId = 1L;
        long accountId = 101L;
        long serverId = 10L;
        String transactionId = "tx123";

        AccountBannedResponse bannedResponse =
                new AccountBannedResponse(1L, new Date(), new Date(), "", "", true);

        AccountGameDetailDto detail = AccountGameDetailDto.builder()
                .id(accountId)
                .username("testUser")
                .email("test@example.com")
                .expansion("WOTLK")
                .online(true)
                .failedLogins("0")
                .joinDate(LocalDate.of(2022, 1, 1))
                .lastIp("192.168.0.1")
                .muteReason(null)
                .muteBy(null)
                .mute(false)
                .lastLogin(LocalDate.of(2023, 12, 1))
                .os("Windows 11")
                .realm("Azeroth")
                .accountBanned(bannedResponse)
                .build();
        when(accountGamePort.account(userId, accountId, serverId, transactionId)).thenReturn(detail);

        // Act
        ResponseEntity<GenericResponse<AccountGameDetailDto>> response = controller.detail(
                transactionId, userId, accountId, serverId
        );

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().username()).isEqualTo("testUser");
        verify(accountGamePort).account(userId, accountId, serverId, transactionId);
    }

    @Test
    void shouldReturnNoContentWhenAccountNotFound() {
        // Arrange
        long userId = 1L;
        long accountId = 101L;
        long serverId = 10L;
        String transactionId = "tx123";

        when(accountGamePort.account(userId, accountId, serverId, transactionId)).thenReturn(null);

        // Act
        ResponseEntity<GenericResponse<AccountGameDetailDto>> response = controller.detail(
                transactionId, userId, accountId, serverId
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.NO_CONTENT);
        verify(accountGamePort).account(userId, accountId, serverId, transactionId);
    }
}
