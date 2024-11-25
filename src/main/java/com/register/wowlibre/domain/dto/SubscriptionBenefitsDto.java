package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.model.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
public class SubscriptionBenefitsDto {
    @NotNull
    private Long serverId;
    @NotNull
    private Long userId;
    @NotNull
    private Long accountId;
    @NotNull
    private Long characterId;
    private List<ItemQuantityModel> items;
    @NotNull
    private String benefitType;
    private Double amount;
}
