package com.register.wowlibre.domain.port.out.user_card;

import com.register.wowlibre.domain.dto.user_card.CardItemDto;
import com.register.wowlibre.domain.dto.user_card.CardWithProbabilityDto;

import java.util.List;

/**
 * Puerto para obtener datos del catálogo de cartas (imagen, nombre, probabilidad) desde la BD.
 */
public interface ObtainCardCatalog {

    /**
     * Devuelve código, URL de imagen y nombre para los códigos indicados.
     */
    List<CardItemDto> findByCodes(List<String> codes);

    /**
     * Devuelve todas las cartas con su probabilidad (1-100) para el sorteo al comprar un sobre.
     */
    List<CardWithProbabilityDto> findAllWithProbability();

    /**
     * Devuelve todo el catálogo (código, imagen, nombre) para que el cliente sepa cuántas cartas hay y su orden.
     * Sin probabilidad; solo para visualización de slots.
     */
    List<CardItemDto> findAllForDisplay();
}
