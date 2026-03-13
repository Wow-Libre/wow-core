package com.register.wowlibre.application.services.user_card;

import com.register.wowlibre.domain.dto.user_card.CardCatalogAdminDto;
import com.register.wowlibre.domain.dto.user_card.CardCatalogAdminRequestDto;
import com.register.wowlibre.domain.port.in.user_card.CardCatalogAdminPort;
import com.register.wowlibre.domain.port.out.user_card.ManageCardCatalog;
import com.register.wowlibre.domain.port.out.user_card.ObtainCardCatalog;
import com.register.wowlibre.infrastructure.entities.CardCatalogEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardCatalogAdminService implements CardCatalogAdminPort {

    private final ObtainCardCatalog obtainCardCatalog;
    private final ManageCardCatalog manageCardCatalog;

    public CardCatalogAdminService(ObtainCardCatalog obtainCardCatalog, ManageCardCatalog manageCardCatalog) {
        this.obtainCardCatalog = obtainCardCatalog;
        this.manageCardCatalog = manageCardCatalog;
    }

    @Override
    public List<CardCatalogAdminDto> listAll(String transactionId) {
        return obtainCardCatalog.findAllForAdmin(transactionId);
    }

    @Override
    public CardCatalogAdminDto create(CardCatalogAdminRequestDto request, String transactionId) {
        if (manageCardCatalog.findByCode(request.getCode(), transactionId).isPresent()) {
            throw new IllegalArgumentException("Card with code already exists: " + request.getCode());
        }
        CardCatalogEntity entity = toEntity(request, null);
        entity.setCreatedAt(LocalDateTime.now());
        CardCatalogEntity saved = manageCardCatalog.save(entity, transactionId);
        return toDto(saved);
    }

    @Override
    public CardCatalogAdminDto update(String code, CardCatalogAdminRequestDto request, String transactionId) {
        CardCatalogEntity existing = manageCardCatalog.findByCode(code, transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + code));
        applyRequestToEntity(request, existing);
        CardCatalogEntity saved = manageCardCatalog.save(existing, transactionId);
        return toDto(saved);
    }

    @Override
    public void delete(String code, String transactionId) {
        if (!manageCardCatalog.findByCode(code, transactionId).isPresent()) {
            throw new IllegalArgumentException("Card not found: " + code);
        }
        manageCardCatalog.deleteByCode(code, transactionId);
    }

    private CardCatalogEntity toEntity(CardCatalogAdminRequestDto request, CardCatalogEntity existing) {
        CardCatalogEntity entity = existing != null ? existing : new CardCatalogEntity();
        if (existing == null) {
            entity.setCode(request.getCode());
        }
        entity.setImageUrl(request.getImageUrl());
        entity.setDisplayName(request.getDisplayName() != null ? request.getDisplayName() : "");
        entity.setProbability(request.getProbability() != null ? request.getProbability() : 50);
        entity.setActive(request.getActive() != null ? request.getActive() : true);
        return entity;
    }

    private void applyRequestToEntity(CardCatalogAdminRequestDto request, CardCatalogEntity entity) {
        toEntity(request, entity);
    }

    private CardCatalogAdminDto toDto(CardCatalogEntity e) {
        return new CardCatalogAdminDto(
                e.getCode(),
                e.getImageUrl(),
                e.getDisplayName(),
                e.getProbability() != null ? e.getProbability() : 50,
                e.getActive() != null ? e.getActive() : true);
    }
}
