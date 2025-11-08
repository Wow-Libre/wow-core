package com.register.wowlibre.service;

import com.register.wowlibre.application.services.promotion.PromotionService;
import com.register.wowlibre.domain.dto.CreatePromotionDto;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.PromotionModel;
import com.register.wowlibre.domain.port.out.promotion.ObtainPromotion;
import com.register.wowlibre.domain.port.out.promotion.SavePromotion;
import com.register.wowlibre.domain.port.out.promotion_item.DeletePromotionItem;
import com.register.wowlibre.domain.port.out.promotion_item.ObtainPromotionItem;
import com.register.wowlibre.domain.port.out.promotion_item.SavePromotionItem;
import com.register.wowlibre.infrastructure.entities.PromotionEntity;
import com.register.wowlibre.infrastructure.entities.PromotionItemEntity;
import com.register.wowlibre.infrastructure.util.RandomString;
import com.register.wowlibre.model.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromotionServiceTest extends BaseTest {

    @Mock
    private ObtainPromotion obtainPromotion;
    @Mock
    private ObtainPromotionItem obtainPromotionItem;
    @Mock
    private SavePromotion savePromotion;
    @Mock
    private SavePromotionItem savePromotionItem;
    @Mock
    private DeletePromotionItem deletePromotionItem;
    @Mock
    private RandomString randomString;

    private PromotionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PromotionService(obtainPromotion, obtainPromotionItem, savePromotion,
                savePromotionItem, deletePromotionItem, randomString);
    }

    @Test
    void findByPromotionServerIdAndClassIdAndLanguage_shouldReturnFilteredPromotions() {
        long realmId = 1L;
        long classId = 1L;
        String language = "es";
        String transactionId = "tx-promo-001";
        PromotionEntity promotion1 = createPromotionEntity(1L, realmId, classId, true);
        PromotionEntity promotion2 = createPromotionEntity(2L, realmId, null, true);
        List<PromotionEntity> promotionsDb = List.of(promotion1, promotion2);
        PromotionItemEntity item = createPromotionItemEntity(1L, promotion1, "ITEM001", 5);

        when(obtainPromotion.findByPromotionRealmIdAndLanguage(realmId, language, transactionId))
                .thenReturn(promotionsDb);
        when(obtainPromotionItem.findByPromotionId(promotion1, transactionId)).thenReturn(List.of(item));
        when(obtainPromotionItem.findByPromotionId(promotion2, transactionId)).thenReturn(Collections.emptyList());

        List<PromotionModel> result = service.findByPromotionServerIdAndClassIdAndLanguage(
                realmId, classId, language, transactionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(obtainPromotion).findByPromotionRealmIdAndLanguage(realmId, language, transactionId);
    }

    @Test
    void findByPromotionServerIdAndClassIdAndLanguage_shouldReturnEmptyWhenNoPromotions() {
        long realmId = 1L;
        long classId = 1L;
        String language = "es";
        String transactionId = "tx-promo-002";

        when(obtainPromotion.findByPromotionRealmIdAndLanguage(realmId, language, transactionId))
                .thenReturn(Collections.emptyList());

        List<PromotionModel> result = service.findByPromotionServerIdAndClassIdAndLanguage(
                realmId, classId, language, transactionId);

        assertTrue(result.isEmpty());
        verify(obtainPromotion).findByPromotionRealmIdAndLanguage(realmId, language, transactionId);
    }

    @Test
    void findByPromotionServerIdAndLanguage_shouldReturnPromotionModel() {
        long id = 1L;
        long realmId = 1L;
        String language = "es";
        String transactionId = "tx-promo-003";
        PromotionEntity promotionEntity = createPromotionEntity(id, realmId, 1L, true);
        PromotionItemEntity item = createPromotionItemEntity(1L, promotionEntity, "ITEM001", 5);

        when(obtainPromotion.findById(id, transactionId)).thenReturn(Optional.of(promotionEntity));
        when(obtainPromotionItem.findByPromotionId(promotionEntity, transactionId)).thenReturn(List.of(item));

        PromotionModel result = service.findByPromotionServerIdAndLanguage(id, realmId, language, transactionId);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(1, result.getItems().size());
        verify(obtainPromotion).findById(id, transactionId);
        verify(obtainPromotionItem).findByPromotionId(promotionEntity, transactionId);
    }

    @Test
    void findByPromotionServerIdAndLanguage_shouldThrowExceptionWhenNotFound() {
        long id = 999L;
        long realmId = 1L;
        String language = "es";
        String transactionId = "tx-promo-004";

        when(obtainPromotion.findById(id, transactionId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.findByPromotionServerIdAndLanguage(id, realmId, language, transactionId)
        );

        assertEquals("The promotion is not found or is not available", exception.getMessage());
        verify(obtainPromotion).findById(id, transactionId);
        verifyNoInteractions(obtainPromotionItem);
    }

    @Test
    void findByPromotionServerId_shouldReturnList() {
        long realmId = 1L;
        String transactionId = "tx-promo-005";
        PromotionEntity promotion = createPromotionEntity(1L, realmId, 1L, true);
        PromotionItemEntity item = createPromotionItemEntity(1L, promotion, "ITEM001", 5);

        when(obtainPromotion.findByPromotionRealmId(realmId, transactionId)).thenReturn(List.of(promotion));
        when(obtainPromotionItem.findByPromotionId(promotion, transactionId)).thenReturn(List.of(item));

        List<PromotionModel> result = service.findByPromotionServerId(realmId, transactionId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(obtainPromotion).findByPromotionRealmId(realmId, transactionId);
    }

    @Test
    void create_shouldCreatePromotionWithItems() {
        String transactionId = "tx-promo-006";
        String reference = "REF-123";
        CreatePromotionDto createPromotionDto = new CreatePromotionDto();
        createPromotionDto.setImgUrl("img.jpg");
        createPromotionDto.setName("Test Promotion");
        createPromotionDto.setDescription("Test Description");
        createPromotionDto.setBtnText("Click");
        createPromotionDto.setSendItem(true);
        createPromotionDto.setType("TYPE");
        createPromotionDto.setMinLevel(1);
        createPromotionDto.setMaxLevel(10);
        createPromotionDto.setAmount(100.0);
        createPromotionDto.setRealmId(1L);
        createPromotionDto.setClassCharacter(1L);
        createPromotionDto.setLevel(5);
        createPromotionDto.setStatus(true);
        createPromotionDto.setLanguage("es");

        CreatePromotionDto.PromotionItemDto itemDto = new CreatePromotionDto.PromotionItemDto("ITEM001", 5);
        createPromotionDto.setItems(List.of(itemDto));

        when(randomString.nextString()).thenReturn(reference);

        ArgumentCaptor<PromotionEntity> promotionCaptor = ArgumentCaptor.forClass(PromotionEntity.class);
        ArgumentCaptor<PromotionItemEntity> itemCaptor = ArgumentCaptor.forClass(PromotionItemEntity.class);

        service.create(createPromotionDto, transactionId);

        verify(randomString).nextString();
        verify(savePromotion).save(promotionCaptor.capture());
        verify(savePromotionItem).save(itemCaptor.capture(), eq(transactionId));

        PromotionEntity savedPromotion = promotionCaptor.getValue();
        assertEquals(reference, savedPromotion.getReference());
        assertEquals("Test Promotion", savedPromotion.getName());
        assertEquals(1, itemCaptor.getAllValues().size());
    }

    @Test
    void create_shouldCreatePromotionWithoutItems() {
        String transactionId = "tx-promo-007";
        String reference = "REF-124";
        CreatePromotionDto createPromotionDto = new CreatePromotionDto();
        createPromotionDto.setImgUrl("img.jpg");
        createPromotionDto.setName("Test Promotion");
        createPromotionDto.setDescription("Test Description");
        createPromotionDto.setBtnText("Click");
        createPromotionDto.setSendItem(false);
        createPromotionDto.setType("TYPE");
        createPromotionDto.setMinLevel(1);
        createPromotionDto.setMaxLevel(10);
        createPromotionDto.setRealmId(1L);
        createPromotionDto.setStatus(true);
        createPromotionDto.setLanguage("es");
        createPromotionDto.setItems(null);

        when(randomString.nextString()).thenReturn(reference);

        service.create(createPromotionDto, transactionId);

        verify(randomString).nextString();
        verify(savePromotion).save(any(PromotionEntity.class));
        verifyNoInteractions(savePromotionItem);
    }

    @Test
    void findActiveByRealmId_shouldReturnActivePromotions() {
        long realmId = 1L;
        String language = "es";
        String transactionId = "tx-promo-008";
        PromotionEntity promotion = createPromotionEntity(1L, realmId, 1L, true);
        PromotionItemEntity item = createPromotionItemEntity(1L, promotion, "ITEM001", 5);

        when(obtainPromotion.findActiveByRealmId(realmId, language, transactionId)).thenReturn(List.of(promotion));
        when(obtainPromotionItem.findByPromotionId(promotion, transactionId)).thenReturn(List.of(item));

        List<PromotionModel> result = service.findActiveByRealmId(realmId, language, transactionId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(obtainPromotion).findActiveByRealmId(realmId, language, transactionId);
    }

    @Test
    void deleteLogical_shouldDeletePromotionAndItems() {
        long promotionId = 1L;
        String transactionId = "tx-promo-009";
        PromotionEntity promotion = createPromotionEntity(promotionId, 1L, 1L, true);
        PromotionItemEntity item1 = createPromotionItemEntity(1L, promotion, "ITEM001", 5);
        PromotionItemEntity item2 = createPromotionItemEntity(2L, promotion, "ITEM002", 3);

        when(obtainPromotion.findById(promotionId, transactionId)).thenReturn(Optional.of(promotion));
        when(obtainPromotionItem.findByPromotionId(promotion, transactionId)).thenReturn(List.of(item1, item2));

        ArgumentCaptor<PromotionEntity> captor = ArgumentCaptor.forClass(PromotionEntity.class);

        service.deleteLogical(promotionId, transactionId);

        verify(obtainPromotion).findById(promotionId, transactionId);
        verify(obtainPromotionItem).findByPromotionId(promotion, transactionId);
        verify(deletePromotionItem).delete(item1, transactionId);
        verify(deletePromotionItem).delete(item2, transactionId);
        verify(savePromotion).save(captor.capture());
        assertFalse(captor.getValue().isStatus());
    }

    @Test
    void deleteLogical_shouldThrowExceptionWhenPromotionNotFound() {
        long promotionId = 999L;
        String transactionId = "tx-promo-010";

        when(obtainPromotion.findById(promotionId, transactionId)).thenReturn(Optional.empty());

        InternalException exception = assertThrows(InternalException.class, () ->
                service.deleteLogical(promotionId, transactionId)
        );

        assertEquals("The promotion is not found", exception.getMessage());
        verify(obtainPromotion).findById(promotionId, transactionId);
        verifyNoInteractions(obtainPromotionItem, deletePromotionItem, savePromotion);
    }

    private PromotionEntity createPromotionEntity(Long id, Long realmId, Long classCharacter, boolean status) {
        PromotionEntity entity = new PromotionEntity();
        entity.setId(id);
        entity.setRealmId(realmId);
        entity.setClassCharacter(classCharacter);
        entity.setStatus(status);
        entity.setName("Test Promotion");
        entity.setDescription("Test Description");
        entity.setImgUrl("img.jpg");
        entity.setReference("REF-" + id);
        entity.setBtnText("Click");
        entity.setSendItem(true);
        entity.setType("TYPE");
        entity.setMinLevel(1);
        entity.setMaxLevel(10);
        entity.setAmount(100.0);
        entity.setLevel(5);
        return entity;
    }

    private PromotionItemEntity createPromotionItemEntity(Long id, PromotionEntity promotion, String code, Integer quantity) {
        PromotionItemEntity entity = new PromotionItemEntity();
        entity.setId(id);
        entity.setPromotionId(promotion);
        entity.setCode(code);
        entity.setQuantity(quantity);
        return entity;
    }
}

