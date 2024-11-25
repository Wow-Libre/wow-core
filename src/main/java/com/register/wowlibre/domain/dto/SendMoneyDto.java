package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class SendMoneyDto {
    @NotNull
    private Long characterId;
    @NotNull
    private Long friendId;
    @NotNull
    private Long accountId;
    @NotNull
    private Long serverId;
    @NotNull
    private Long money;
}
