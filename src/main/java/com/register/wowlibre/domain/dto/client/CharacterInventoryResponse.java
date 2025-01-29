package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
public class CharacterInventoryResponse {
    @JsonProperty("character_id")
    private Long characterId;
    private Long bag;
    private Long slot;
    private Long item;
    @JsonProperty("item_id")
    private Long itemId;
    @JsonProperty("instance_id")
    private Long instanceId;
    private String name;
}
