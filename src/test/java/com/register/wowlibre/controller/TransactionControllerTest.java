package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.CreateTransactionItemsDto;
import com.register.wowlibre.domain.dto.SubscriptionBenefitsDto;
import com.register.wowlibre.domain.dto.PromotionsDto;
import com.register.wowlibre.domain.dto.ClaimPromoDto;
import com.register.wowlibre.domain.port.in.transaction.TransactionPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.TransactionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionControllerTest {

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldPurchaseItems() {
        String transactionId = "tx-tx-001";
        CreateTransactionItemsDto request = new CreateTransactionItemsDto();
        request.setServerId(1L);
        request.setUserId(1L);
        request.setAccountId(101L);
        request.setReference("REF-123");

        ResponseEntity<GenericResponse<Void>> response = controller.sendItems(transactionId, request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(transactionPort).purchase(request.getServerId(), request.getUserId(), request.getAccountId(),
                request.getReference(), request.getItems(), request.getAmount(), transactionId);
    }

    @Test
    void shouldSendSubscriptionBenefits() {
        String transactionId = "tx-tx-002";
        SubscriptionBenefitsDto request = new SubscriptionBenefitsDto(1L, 1L, 101L, 201L,
                List.of(), "MONTHLY", 100.0);

        ResponseEntity<GenericResponse<Void>> response = controller.sendSubscriptionBenefits(transactionId, request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(transactionPort).sendSubscriptionBenefits(request.getServerId(), request.getUserId(),
                request.getAccountId(), request.getCharacterId(), request.getItems(),
                request.getBenefitType(), request.getAmount(), transactionId);
    }

    @Test
    void shouldGetPromotions() {
        String transactionId = "tx-tx-003";
        Locale locale = Locale.ENGLISH;
        Long userId = 1L;
        Long accountId = 101L;
        Long serverId = 1L;
        Long characterId = 201L;
        Long classId = 1L;
        PromotionsDto promotionsDto = new PromotionsDto(List.of(), 0);

        when(transactionPort.getPromotions(serverId, userId, accountId, characterId, classId,
                locale.getLanguage(), transactionId)).thenReturn(promotionsDto);

        ResponseEntity<GenericResponse<PromotionsDto>> response = controller.promotions(
                transactionId, locale, userId, accountId, serverId, characterId, classId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(transactionPort).getPromotions(serverId, userId, accountId, characterId, classId,
                locale.getLanguage(), transactionId);
    }

    @Test
    void shouldClaimPromotions() {
        String transactionId = "tx-tx-004";
        Locale locale = Locale.ENGLISH;
        Long userId = 1L;
        ClaimPromoDto request = new ClaimPromoDto();
        request.setServerId(1L);
        request.setAccountId(101L);
        request.setCharacterId(201L);
        request.setPromotionId(1L);

        ResponseEntity<GenericResponse<Void>> response = controller.claimPromotions(transactionId, locale, userId, request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(transactionPort).claimPromotion(request.getServerId(), userId, request.getAccountId(),
                request.getCharacterId(), request.getPromotionId(), locale.getLanguage(), transactionId);
    }
}

