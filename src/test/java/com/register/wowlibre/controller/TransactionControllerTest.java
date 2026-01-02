package com.register.wowlibre.controller;

import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.controller.*;
import com.register.wowlibre.infrastructure.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private SignatureService signatureService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldPurchaseItems() throws Exception {
        String transactionId = "tx-tx-001";
        String signature = "valid-signature";
        CreateTransactionItemsDto request = new CreateTransactionItemsDto();
        request.setRealmId(1L);
        request.setUserId(1L);
        request.setAccountId(101L);
        request.setReference("REF-123");
        String requestBodyJson = "{\"realm_id\":1,\"user_id\":1,\"account_id\":101,\"reference\":\"REF-123\"}";

        when(objectMapper.writeValueAsString(request)).thenReturn(requestBodyJson);
        when(signatureService.validateSignature(requestBodyJson, signature)).thenReturn(true);

        ResponseEntity<GenericResponse<Void>> response = controller.sendItems(transactionId, signature, request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(objectMapper).writeValueAsString(request);
        verify(signatureService).validateSignature(requestBodyJson, signature);
        verify(transactionPort).purchase(request.getRealmId(), request.getUserId(), request.getAccountId(),
                request.getReference(), request.getItems(), request.getAmount(), transactionId);
    }

    @Test
    void shouldSendSubscriptionBenefits() throws Exception {
        String transactionId = "tx-tx-002";
        String signature = "valid-signature";
        SubscriptionBenefitsDto request = new SubscriptionBenefitsDto(1L, 1L, 101L, 201L,
                List.of(), "MONTHLY", 100.0);
        String requestBodyJson = "{\"realm_id\":1,\"user_id\":1}";

        when(objectMapper.writeValueAsString(request)).thenReturn(requestBodyJson);
        when(signatureService.validateSignature(requestBodyJson, signature)).thenReturn(true);

        ResponseEntity<GenericResponse<Void>> response = controller.sendSubscriptionBenefits(transactionId, signature
                , request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(objectMapper).writeValueAsString(request);
        verify(signatureService).validateSignature(requestBodyJson, signature);
        verify(transactionPort).sendSubscriptionBenefits(request.getRealmId(), request.getUserId(),
                request.getAccountId(), request.getCharacterId(), request.getItems(),
                request.getBenefitType(), request.getAmount(), transactionId);
    }

    @Test
    void shouldSendSubscriptionBenefits_invalidSignature_returnsInternalServerError() throws Exception {
        String transactionId = "tx-tx-002";
        String signature = "invalid-signature";
        SubscriptionBenefitsDto request = new SubscriptionBenefitsDto(1L, 1L, 101L, 201L,
                List.of(), "MONTHLY", 100.0);
        String requestBodyJson = "{\"realm_id\":1,\"user_id\":1}";

        when(objectMapper.writeValueAsString(request)).thenReturn(requestBodyJson);
        when(signatureService.validateSignature(requestBodyJson, signature)).thenReturn(false);

        ResponseEntity<GenericResponse<Void>> response = controller.sendSubscriptionBenefits(transactionId, signature
                , request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(objectMapper).writeValueAsString(request);
        verify(signatureService).validateSignature(requestBodyJson, signature);
    }

    @Test
    void shouldGetPromotions() {
        String transactionId = "tx-tx-003";
        Locale locale = Locale.ENGLISH;
        long userId = 1L;
        long accountId = 101L;
        long serverId = 1L;
        long characterId = 201L;
        long classId = 1L;
        PromotionsDto promotionsDto = new PromotionsDto(List.of(), 0);

        when(transactionPort.getPromotions(serverId, userId, accountId, characterId, classId,
                locale.getLanguage(), transactionId)).thenReturn(promotionsDto);

        ResponseEntity<GenericResponse<PromotionsDto>> response = controller.promotions(
                transactionId, locale, userId, accountId, serverId, characterId, classId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(promotionsDto, response.getBody().getData());
        verify(transactionPort).getPromotions(serverId, userId, accountId, characterId, classId,
                locale.getLanguage(), transactionId);
    }

    @Test
    void shouldClaimPromotions() {
        String transactionId = "tx-tx-004";
        Locale locale = Locale.ENGLISH;
        long userId = 1L;
        ClaimPromoDto request = new ClaimPromoDto();
        request.setServerId(1L);
        request.setAccountId(101L);
        request.setCharacterId(201L);
        request.setPromotionId(1L);

        ResponseEntity<GenericResponse<Void>> response = controller.claimPromotions(transactionId, locale, userId,
                request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(transactionPort).claimPromotion(request.getServerId(), userId, request.getAccountId(),
                request.getCharacterId(), request.getPromotionId(), locale.getLanguage(), transactionId);
    }
}

