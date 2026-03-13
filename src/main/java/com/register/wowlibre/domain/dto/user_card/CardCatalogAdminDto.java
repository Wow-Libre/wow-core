package com.register.wowlibre.domain.dto.user_card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCatalogAdminDto {

    private String code;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("display_name")
    private String displayName;

    private Integer probability;
    private Boolean active;
}
