package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
public class UpdateGuildRequest {
    @NotNull
    private String discord;
    @JsonProperty("is_public")
    private boolean isPublic;
    @JsonProperty("multi_faction")
    private boolean multiFaction;
    @NotNull
    @JsonProperty("account_id")
    private Long accountId;
    @JsonProperty("character_id")
    @NotNull
    private Long characterId;
}
