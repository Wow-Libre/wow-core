package com.register.wowlibre.domain.dto.client;

import com.register.wowlibre.domain.model.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
public class SubscriptionBenefitsRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long accountId;
    @NotNull
    private Long characterId;

    private List<ItemQuantityModel> items;
    @NotNull
    @NotEmpty
    private String benefitType;
    private Double amount;
}
