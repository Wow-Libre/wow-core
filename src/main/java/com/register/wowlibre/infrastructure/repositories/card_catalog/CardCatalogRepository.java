package com.register.wowlibre.infrastructure.repositories.card_catalog;

import com.register.wowlibre.infrastructure.entities.CardCatalogEntity;

import java.util.List;

public interface CardCatalogRepository extends org.springframework.data.jpa.repository.JpaRepository<CardCatalogEntity, String> {

    List<CardCatalogEntity> findByCodeIn(List<String> codes);
}
