package com.register.wowlibre.domain.port.in.user_card;

import com.register.wowlibre.domain.dto.user_card.CardItemDto;

import java.util.List;

/**
 * Caso de uso: comprar un sobre (200 puntos). Devuelve 3 cartas sorteadas por probabilidad.
 */
public interface BuyPackPort {

    /**
     * Compra un sobre: descuenta 200 puntos y devuelve 3 cartas (según probabilidad del catálogo).
     * Lanza BadRequestException si saldo insuficiente o catálogo vacío.
     */
    List<CardItemDto> buyPack(Long userId, String transactionId);
}
