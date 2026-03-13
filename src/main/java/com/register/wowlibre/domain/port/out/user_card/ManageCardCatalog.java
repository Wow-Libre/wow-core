package com.register.wowlibre.domain.port.out.user_card;

import com.register.wowlibre.infrastructure.entities.CardCatalogEntity;

import java.util.Optional;

/**
 * Puerto para crear, actualizar y eliminar entradas del catálogo de cartas.
 */
public interface ManageCardCatalog {

    CardCatalogEntity save(CardCatalogEntity entity, String transactionId);

    Optional<CardCatalogEntity> findByCode(String code, String transactionId);

    void deleteByCode(String code, String transactionId);
}
