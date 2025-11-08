package com.register.wowlibre.controller;

import com.register.wowlibre.domain.dto.CreatePromotionDto;
import com.register.wowlibre.domain.model.PromotionModel;
import com.register.wowlibre.domain.port.in.promotion.PromotionPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.infrastructure.controller.PromotionsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PromotionsControllerTest {

    @Mock
    private PromotionPort promotionPort;

    @InjectMocks
    private PromotionsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreatePromotion() {
        String transactionId = "tx-promo-001";
        CreatePromotionDto createPromotionDto = new CreatePromotionDto();

        ResponseEntity<GenericResponse<Void>> response = controller.create(transactionId, createPromotionDto);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(promotionPort).create(createPromotionDto, transactionId);
    }

    @Test
    void shouldReturnActivePromotions() {
        String transactionId = "tx-promo-002";
        Long realmId = 1L;
        String language = "es";
        PromotionModel promotion = PromotionModel.builder().id(1L).build();
        List<PromotionModel> promotions = List.of(promotion);

        when(promotionPort.findActiveByRealmId(realmId, language, transactionId)).thenReturn(promotions);

        ResponseEntity<GenericResponse<List<PromotionModel>>> response = controller.findActive(transactionId, realmId, language);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        verify(promotionPort).findActiveByRealmId(realmId, language, transactionId);
    }

    @Test
    void shouldReturnNoContentWhenNoActivePromotions() {
        String transactionId = "tx-promo-003";
        Long realmId = 1L;
        String language = "es";
        List<PromotionModel> promotions = Collections.emptyList();

        when(promotionPort.findActiveByRealmId(realmId, language, transactionId)).thenReturn(promotions);

        ResponseEntity<GenericResponse<List<PromotionModel>>> response = controller.findActive(transactionId, realmId, language);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(promotionPort).findActiveByRealmId(realmId, language, transactionId);
    }

    @Test
    void shouldDeletePromotion() {
        String transactionId = "tx-promo-004";
        Long id = 1L;

        ResponseEntity<GenericResponse<Void>> response = controller.delete(transactionId, id);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        verify(promotionPort).deleteLogical(id, transactionId);
    }
}

