package com.register.wowlibre.domain.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class CharacterProfessionsDto {
    private Long id;
    private String logo;
    private String name;
    private Long value;
    private Long max;
}
