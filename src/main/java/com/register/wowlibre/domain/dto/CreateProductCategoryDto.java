package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class CreateProductCategoryDto {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String disclaimer;
}
