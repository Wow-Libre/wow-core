package com.register.wowlibre.domain.dto.guild_benefits_catalog;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Builder
public class GuildBenefitsCatalogDto {
    public final Long id;
    public final String title;
    @JsonProperty("sub_title")
    public final String subTitle;
    public final String description;
    public final String imageUrl;
    @JsonProperty("item_id")
    public final String coreCode;
    public final Integer quantity;
    public final boolean isActive;
    public final String externalUrl;
}
