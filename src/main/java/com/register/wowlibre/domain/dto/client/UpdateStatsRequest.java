package com.register.wowlibre.domain.dto.client;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
public class UpdateStatsRequest {
    @NotNull
    private String type;
    @NotNull
    private String reference;
    @NotNull
    private Long userId;
    @NotNull
    private Long accountId;
    @NotNull
    private Long characterId;
}

