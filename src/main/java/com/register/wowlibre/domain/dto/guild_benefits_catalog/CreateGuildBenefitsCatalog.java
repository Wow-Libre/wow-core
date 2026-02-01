package com.register.wowlibre.domain.dto.guild_benefits_catalog;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class CreateGuildBenefitsCatalog {
    @NotNull
    private String subTitle;
    @NotNull
    private String description;
    @NotNull
    private String imageUrl;
    @NotNull
    private String coreCode;
    @NotNull
    private Integer quantity;
    @NotNull
    private String externalUrl;
    @NotNull
    private String title;
}
