package com.register.wowlibre.domain.dto.client;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@Data
public class DeductTokensDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long accountId;
    @NotNull
    private Long characterId;
    @NotNull
    private Long points;
}
