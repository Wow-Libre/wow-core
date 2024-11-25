package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@Getter
public class BankCollectGoldRequest {
    @JsonProperty("user_id")
    private Long userId;
    private Double amount;
}
