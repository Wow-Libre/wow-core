package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
public class UpdateStatsDto {
    @NotNull
    private String type;
    @NotNull
    private String reference;
    @NotNull
    @JsonProperty("account_id")
    private Long accountId;
    @NotNull
    @JsonProperty("character_id")
    private Long characterId;
}

