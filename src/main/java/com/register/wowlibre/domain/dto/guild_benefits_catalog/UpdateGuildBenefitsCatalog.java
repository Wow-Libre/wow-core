package com.register.wowlibre.domain.dto.guild_benefits_catalog;

import lombok.*;

@Data
public class UpdateGuildBenefitsCatalog {
    private Long benefitId;
    private String subTitle;
    private String description;
    private String imageUrl;
    private String coreCode;
    private Integer quantity;
    private String externalUrl;
    private String title;
}
