package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Data
public class CreatePromotionDto {


    @NotNull
    @NotBlank
    private String imgUrl;

    @NotNull
    @NotBlank
    @Size(max = 30)
    private String name;

    @NotNull
    @NotBlank
    @Size(max = 80)
    private String description;

    @NotNull
    @NotBlank
    @Size(max = 30)
    @JsonProperty("btn_text")
    private String btnText;

    @NotNull
    @JsonProperty("send_item")
    private Boolean sendItem;

    @NotNull
    @NotBlank
    @Size(max = 30)
    private String type;

    @NotNull
    @Min(0)
    @JsonProperty("min_level")
    private Integer minLevel;

    @NotNull
    @Min(0)
    @JsonProperty("max_level")
    private Integer maxLevel;

    private Double amount;

    @NotNull
    @JsonProperty("realm_id")
    private Long realmId;

    @JsonProperty("class_character")
    private Long classCharacter;

    private Integer level;

    @NotNull
    private Boolean status;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 2)
    private String language;

    private List<PromotionItemDto> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PromotionItemDto {
        @NotNull
        @NotBlank
        @Size(max = 30)
        private String code;

        @NotNull
        @Min(1)
        private Integer quantity;
    }
}
