package com.register.wowlibre.domain.dto.guild_benefits_catalog;

import lombok.*;

@Builder
public class GuildBenefitsCatalogDto {
    public final Long id;
    public final String title;
    public final String subTitle;
    public final String description;
    public final String imageUrl;
    public final String coreCode;
    public final Integer quantity;
    public final boolean isActive;
    public final String externalUrl;
}
