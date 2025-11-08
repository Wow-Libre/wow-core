package com.register.wowlibre.application.services.promotion;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.promotion.*;
import com.register.wowlibre.domain.port.out.promotion.*;
import com.register.wowlibre.domain.port.out.promotion_item.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class PromotionService implements PromotionPort {
    private final ObtainPromotion obtainPromotion;
    private final ObtainPromotionItem obtainPromotionItem;
    private final SavePromotion savePromotion;
    private final SavePromotionItem savePromotionItem;
    private final DeletePromotionItem deletePromotionItem;
    private final RandomString randomString;

    public PromotionService(ObtainPromotion obtainPromotion, ObtainPromotionItem obtainPromotionItem,
                            SavePromotion savePromotion, SavePromotionItem savePromotionItem,
                            DeletePromotionItem deletePromotionItem,
                            @Qualifier("reference-serial-bank") RandomString randomString) {
        this.obtainPromotion = obtainPromotion;
        this.obtainPromotionItem = obtainPromotionItem;
        this.savePromotion = savePromotion;
        this.savePromotionItem = savePromotionItem;
        this.deletePromotionItem = deletePromotionItem;
        this.randomString = randomString;
    }

    @Override
    public List<PromotionModel> findByPromotionServerIdAndClassIdAndLanguage(Long realmId, Long classId,
                                                                             String language, String transactionId) {
        List<PromotionModel> promotions = new ArrayList<>();

        List<PromotionEntity> promotionsDb = obtainPromotion.findByPromotionRealmIdAndLanguage(realmId, language,
                transactionId);

        if (promotionsDb.isEmpty()) {
            return promotions;
        }

        List<PromotionEntity> filterClassPromotional = promotionsDb.stream()
                .filter(promo -> promo.getClassCharacter() == null
                        || promo.getClassCharacter() == 0
                        || promo.getClassCharacter().equals(classId))
                .toList();

        if (filterClassPromotional.isEmpty()) {
            return promotions;
        }

        promotionsBuild(transactionId, filterClassPromotional, promotions);

        return promotions;
    }

    @Override
    public PromotionModel findByPromotionServerIdAndLanguage(Long id, Long realmId, String language,
                                                             String transactionId) {

        Optional<PromotionEntity> promotionsDb = obtainPromotion.findById(id, transactionId);

        if (promotionsDb.isEmpty()) {
            throw new InternalException("The promotion is not found or is not available", transactionId);
        }

        PromotionEntity promotionEntity = promotionsDb.get();

        List<PromotionItemEntity> promotionItem = obtainPromotionItem.findByPromotionId(promotionEntity,
                transactionId);

        return PromotionModel.builder()
                .id(promotionEntity.getId())
                .amount(promotionEntity.getAmount())
                .reference(promotionEntity.getReference())
                .img(promotionEntity.getImgUrl())
                .name(promotionEntity.getName())
                .description(promotionEntity.getDescription())
                .btnTxt(promotionEntity.getBtnText())
                .sendItem(promotionEntity.isSendItem())
                .type(promotionEntity.getType())
                .minLvl(promotionEntity.getMinLevel())
                .maxLvl(promotionEntity.getMaxLevel())
                .level(promotionEntity.getLevel())
                .status(promotionEntity.isStatus())
                .realmId(promotionEntity.getRealmId())
                .items(promotionItem.stream().map(this::promotionItemMapToModel).toList())
                .classId(promotionEntity.getClassCharacter())
                .build();
    }

    @Override
    public List<PromotionModel> findByPromotionServerId(Long realmId, String transactionId) {
        List<PromotionModel> promotions = new ArrayList<>();

        List<PromotionEntity> promotionDb = obtainPromotion.findByPromotionRealmId(realmId, transactionId);

        promotionsBuild(transactionId, promotionDb, promotions);

        return promotions;
    }

    private void promotionsBuild(String transactionId, List<PromotionEntity> promotionDb,
                                 List<PromotionModel> promotions) {
        for (PromotionEntity promotionEntity : promotionDb) {
            List<PromotionItemEntity> promotionItem = obtainPromotionItem.findByPromotionId(promotionEntity,
                    transactionId);

            promotions.add(PromotionModel.builder()
                    .id(promotionEntity.getId())
                    .amount(promotionEntity.getAmount())
                    .reference(promotionEntity.getReference())
                    .img(promotionEntity.getImgUrl())
                    .name(promotionEntity.getName())
                    .description(promotionEntity.getDescription())
                    .btnTxt(promotionEntity.getBtnText())
                    .sendItem(promotionEntity.isSendItem())
                    .level(promotionEntity.getLevel())
                    .status(promotionEntity.isStatus())
                    .type(promotionEntity.getType())
                    .minLvl(promotionEntity.getMinLevel())
                    .maxLvl(promotionEntity.getMaxLevel())
                    .realmId(promotionEntity.getRealmId())
                    .items(promotionItem.stream().map(this::promotionItemMapToModel).toList())
                    .classId(promotionEntity.getClassCharacter())
                    .build());
        }
    }

    private PromotionModel.Items promotionItemMapToModel(PromotionItemEntity promotionItem) {
        return new PromotionModel.Items(promotionItem.getCode(), promotionItem.getQuantity());
    }

    @Override
    public void create(CreatePromotionDto createPromotionDto, String transactionId) {
        // Crear la entidad de promoción
        PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.setReference(randomString.nextString());
        promotionEntity.setImgUrl(createPromotionDto.getImgUrl());
        promotionEntity.setName(createPromotionDto.getName());
        promotionEntity.setDescription(createPromotionDto.getDescription());
        promotionEntity.setBtnText(createPromotionDto.getBtnText());
        promotionEntity.setSendItem(createPromotionDto.getSendItem() != null && createPromotionDto.getSendItem());
        promotionEntity.setType(createPromotionDto.getType());
        promotionEntity.setMinLevel(createPromotionDto.getMinLevel());
        promotionEntity.setMaxLevel(createPromotionDto.getMaxLevel());
        promotionEntity.setAmount(createPromotionDto.getAmount());
        promotionEntity.setRealmId(createPromotionDto.getRealmId());
        promotionEntity.setClassCharacter(createPromotionDto.getClassCharacter());
        promotionEntity.setLevel(createPromotionDto.getLevel());
        promotionEntity.setStatus(createPromotionDto.getStatus() != null && createPromotionDto.getStatus());
        promotionEntity.setLanguage(createPromotionDto.getLanguage());

        // Guardar la promoción para obtener el ID generado
        savePromotion.save(promotionEntity);

        // Guardar los items de la promoción si existen
        if (createPromotionDto.getItems() != null && !createPromotionDto.getItems().isEmpty()) {
            for (CreatePromotionDto.PromotionItemDto itemDto : createPromotionDto.getItems()) {
                PromotionItemEntity promotionItemEntity = new PromotionItemEntity();
                promotionItemEntity.setCode(itemDto.getCode());
                promotionItemEntity.setQuantity(itemDto.getQuantity());
                promotionItemEntity.setPromotionId(promotionEntity);
                savePromotionItem.save(promotionItemEntity, transactionId);
            }
        }
    }

    @Override
    public List<PromotionModel> findActiveByRealmId(Long realmId, String language, String transactionId) {
        List<PromotionModel> promotions = new ArrayList<>();

        List<PromotionEntity> promotionDb = obtainPromotion.findActiveByRealmId(realmId, language, transactionId);

        promotionsBuild(transactionId, promotionDb, promotions);

        return promotions;
    }

    @Override
    public void deleteLogical(Long promotionId, String transactionId) {
        Optional<PromotionEntity> promotionEntityOpt = obtainPromotion.findById(promotionId, transactionId);

        if (promotionEntityOpt.isEmpty()) {
            throw new InternalException("The promotion is not found", transactionId);
        }

        PromotionEntity promotionEntity = promotionEntityOpt.get();

        // Buscar y eliminar los items asociados a la promoción
        List<PromotionItemEntity> promotionItems = obtainPromotionItem.findByPromotionId(promotionEntity,
                transactionId);
        if (promotionItems != null && !promotionItems.isEmpty()) {
            for (PromotionItemEntity item : promotionItems) {
                deletePromotionItem.delete(item, transactionId);
            }
        }

        // Realizar delete lógico de la promoción
        promotionEntity.setStatus(false);
        savePromotion.save(promotionEntity);
    }
}
