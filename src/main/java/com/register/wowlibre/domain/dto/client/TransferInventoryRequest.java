package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@Data
public class TransferInventoryRequest {
    @JsonProperty("character_id")
    private Long characterId;
    @JsonProperty("friend_id")
    private Long friendId;
    @JsonProperty("item_id")
    private Long itemId;
    private Integer quantity;
    @JsonProperty("account_id")
    private Long accountId;
}
