package com.register.wowlibre.domain.dto.user_card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item de carta con código y URL de imagen (y opcional nombre).
 * La URL permite agregar/cambiar imágenes desde el backend sin actualizar la app.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardItemDto {

    private String code;
    private String imageUrl;
    private String name;
}
