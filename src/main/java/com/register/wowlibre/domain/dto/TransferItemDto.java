package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class TransferItemDto {
    @NotNull
    private Long characterId;
    @NotNull
    private Long friendId;
    @NotNull
    private Long itemId;
    @NotNull
    private Integer count;
    @NotNull
    private Long accountId;
    @NotNull
    private Long serverId;
}
