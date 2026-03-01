package com.register.wowlibre.domain.dto.user_card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item de carta con código, URL de imagen, nombre y opcional cantidad (copias que tiene el usuario).
 * quantity es null en respuestas de catálogo; en "mis cartas" siempre >= 1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardItemDto {

    private String code;
    private String imageUrl;
    private String name;
    /** Copias que tiene el usuario; null en catálogo. */
    private Integer quantity;
}
