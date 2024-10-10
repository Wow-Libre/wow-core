package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
public class SendLevelRequest {
    @JsonProperty("character_id")
    public Long characterId;
    @JsonProperty("friend_id")
    public Long friendId;
    @JsonProperty("account_id")
    public Long accountId;
    @JsonProperty("user_id")
    public Long userId;
    public Integer level;
    public Double cost;
}
