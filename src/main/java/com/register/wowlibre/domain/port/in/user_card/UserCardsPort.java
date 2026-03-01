package com.register.wowlibre.domain.port.in.user_card;

import com.register.wowlibre.domain.dto.user_card.CardItemDto;

import java.util.List;

/**
 * Puerto para obtener las cartas descubiertas del usuario (código + URL de imagen + nombre).
 */
public interface UserCardsPort {

    /**
     * Devuelve las cartas descubiertas con código, URL de imagen y nombre (desde catálogo).
     */
    List<CardItemDto> getDiscoveredCards(Long userId, String transactionId);
}
