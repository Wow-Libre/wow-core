package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.PromotionModel;
import com.register.wowlibre.domain.port.in.dashboard.DashboardPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.DashboardController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardControllerTest {

    @Mock
    private DashboardPort dashboardPort;

    @InjectMocks
    private DashboardController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCreditsLoans() {
        long userId = 1L;
        long serverId = 1L;
        int size = 10;
        int page = 0;
        String filter = "ALL";
        boolean asc = true;
        String transactionId = "tx-dash-001";
        LoansDto loansDto = new LoansDto();

        when(dashboardPort.creditLoans(userId, serverId, size, page, filter, asc, transactionId))
                .thenReturn(loansDto);

        ResponseEntity<GenericResponse<LoansDto>> response = controller.creditsLoans(
                transactionId, userId, serverId, size, page, filter, asc
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).creditLoans(userId, serverId, size, page, filter, asc, transactionId);
    }

    @Test
    void shouldEnableLoan() {
        long userId = 1L;
        long serverId = 1L;
        Double loans = 1000.0;
        String service = "BANK";
        String transactionId = "tx-dash-002";

        ResponseEntity<GenericResponse<Void>> response = controller.enableLoan(
                transactionId, userId, serverId, loans, service
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).enableLoan(userId, serverId, loans, service, transactionId);
    }

    @Test
    void shouldReturnLoansForLineChart() {
        long userId = 1L;
        long serverId = 1L;
        String transactionId = "tx-dash-003";
        Map<Integer, Map<String, Map<Integer, Map<String, Integer>>>> loansData = new HashMap<>();

        when(dashboardPort.groupLoansAndPaymentsByDate(userId, serverId, transactionId))
                .thenReturn(loansData);

        ResponseEntity<GenericResponse<Map<Integer, Map<String, Map<Integer, Map<String, Integer>>>>>> response =
                controller.getLoansForLineChart(transactionId, userId, serverId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).groupLoansAndPaymentsByDate(userId, serverId, transactionId);
    }

    @Test
    void shouldReturnAccountsServer() {
        long userId = 1L;
        long serverId = 1L;
        int size = 10;
        int page = 0;
        String filter = "ALL";
        String transactionId = "tx-dash-004";
        AccountsGameDto accountsGameDto = new AccountsGameDto(new ArrayList<>(), 0L);

        when(dashboardPort.accountsServer(userId, serverId, filter, size, page, transactionId))
                .thenReturn(accountsGameDto);

        ResponseEntity<GenericResponse<AccountsGameDto>> response = controller.accountsServer(
                transactionId, userId, serverId, size, page, filter
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).accountsServer(userId, serverId, filter, size, page, transactionId);
    }

    @Test
    void shouldReturnAccountUpdate() {
        long userId = 1L;
        long serverId = 1L;
        int size = 10;
        int page = 0;
        String filter = "ALL";
        String transactionId = "tx-dash-005";
        AccountsGameDto accountsGameDto = new AccountsGameDto(new ArrayList<>(), 0L);

        when(dashboardPort.accountsServer(userId, serverId, filter, size, page, transactionId))
                .thenReturn(accountsGameDto);

        ResponseEntity<GenericResponse<AccountsGameDto>> response = controller.accountUpdate(
                transactionId, userId, serverId, size, page, filter
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).accountsServer(userId, serverId, filter, size, page, transactionId);
    }

    @Test
    void shouldReturnDashboard() {
        long userId = 1L;
        long serverId = 1L;
        String transactionId = "tx-dash-006";
        DashboardMetricsDto metrics = new DashboardMetricsDto(1000L, 100L, 50L, 10L, 500L, 200L, 300L, 5L, new ArrayList<>());

        when(dashboardPort.metrics(userId, serverId, transactionId)).thenReturn(metrics);

        ResponseEntity<GenericResponse<DashboardMetricsDto>> response = controller.dashboard(
                transactionId, userId, serverId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).metrics(userId, serverId, transactionId);
    }

    @Test
    void shouldUpdateMailAccount() {
        long userId = 1L;
        long serverId = 1L;
        String transactionId = "tx-dash-007";
        AccountUpdateMailDto request = new AccountUpdateMailDto();
        request.setUsername("testuser");
        request.setMail("test@example.com");

        ResponseEntity<GenericResponse<Void>> response = controller.updateMailAccount(
                transactionId, userId, serverId, request
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).updateMail(userId, serverId, request.getUsername(),
                request.getMail(), transactionId);
    }

    @Test
    void shouldReturnPromotions() {
        long userId = 1L;
        long serverId = 1L;
        String transactionId = "tx-dash-008";
        List<PromotionModel> promotions = new ArrayList<>();

        when(dashboardPort.getPromotions(userId, serverId, transactionId)).thenReturn(promotions);

        ResponseEntity<GenericResponse<List<PromotionModel>>> response = controller.promotions(
                transactionId, userId, serverId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).getPromotions(userId, serverId, transactionId);
    }

    @Test
    void shouldBanAccount() {
        long userId = 1L;
        String transactionId = "tx-dash-009";
        AccountBanDto request = new AccountBanDto();

        ResponseEntity<GenericResponse<Void>> response = controller.accountBan(
                transactionId, userId, request
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).bannedUser(request, userId, transactionId);
    }

    @Test
    void shouldReturnConfigsServer() {
        long userId = 1L;
        String transactionId = "tx-dash-010";
        EmulatorConfigDto request = new EmulatorConfigDto();
        request.setServerId(1L);
        request.setRoute("test-route");
        request.setAuthServer(true);
        Map<String, String> configs = new HashMap<>();

        when(dashboardPort.getConfigs(userId, request.getServerId(), request.getRoute(),
                request.isAuthServer(), transactionId)).thenReturn(configs);

        ResponseEntity<GenericResponse<Map<String, String>>> response = controller.configsServer(
                transactionId, userId, request
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        verify(dashboardPort).getConfigs(userId, request.getServerId(), request.getRoute(),
                request.isAuthServer(), transactionId);
    }
}

