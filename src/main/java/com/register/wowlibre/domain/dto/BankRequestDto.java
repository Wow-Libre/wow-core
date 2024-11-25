package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class BankRequestDto {
    @NotNull
    private Long planId;
    @NotNull
    private Long accountId;
    @NotNull
    private Long characterId;
    @NotNull
    private Long serverId;
}
