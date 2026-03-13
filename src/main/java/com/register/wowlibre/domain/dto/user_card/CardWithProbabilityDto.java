package com.register.wowlibre.domain.dto.user_card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Carta con probabilidad para el sorteo al comprar un sobre (1-100).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardWithProbabilityDto {

    private String code;
    private int probability;
}
