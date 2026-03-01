package com.register.wowlibre.domain.dto.user_card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta con las cartas descubiertas: código, URL de imagen y nombre.
 * El cliente usa imageUrl para mostrar la imagen (permite agregar/cambiar cartas desde el backend).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCardsDto {

    private List<CardItemDto> cards;
}
