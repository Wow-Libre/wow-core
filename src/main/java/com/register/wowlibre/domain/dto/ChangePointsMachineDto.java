package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ChangePointsMachineDto {
    @NotNull
    private Long realmId;

    @NotNull
    private Long accountId;

    @NotNull
    private Long characterId;

    @NotNull
    @Min(1)
    private Long points;

    @NotNull
    @NotBlank
    private String type;
}
