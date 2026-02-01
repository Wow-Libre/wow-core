package com.register.wowlibre.domain.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class ProductCategoryDto {
    private Long id;
    private String name;
    private String description;
    private String disclaimer;
}
