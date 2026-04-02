package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentSubscriptionEnvelopeDto {
    @JsonProperty("active")
    private boolean active;
    @JsonProperty("subscription")
    private CurrentSubscriptionDto subscription;
}
