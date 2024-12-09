package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class UpdateGuildDto {

    private String discord;
    @JsonProperty("is_public")
    private boolean isPublic;

    private boolean multiFaction;
    @NotNull
    private Long accountId;
    @NotNull
    private Long characterId;
    @NotNull
    private Long serverId;
}
