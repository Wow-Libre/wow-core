package com.register.wowlibre.domain.dto.user_card;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCatalogAdminRequestDto {

    @NotBlank(message = "code is required")
    @Size(min = 1, max = 32)
    private String code;

    @NotBlank(message = "image_url is required")
    @Size(min = 1, max = 512)
    @JsonProperty("image_url")
    private String imageUrl;

    @Size(max = 128)
    @JsonProperty("display_name")
    private String displayName;

    @Min(1)
    @Max(100)
    private Integer probability = 50;

    private Boolean active = true;
}
