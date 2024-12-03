package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@Data
public class ClaimMachineRequest {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("account_id")
    private Long accountId;
    @JsonProperty("character_id")
    private Long characterId;
    private String type;

}
