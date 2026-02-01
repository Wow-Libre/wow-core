package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ClaimSubscriptionBenefitsDto {
    @NotNull
    private Long serverId;
    @NotNull
    private Long accountId;
    @NotNull
    private Long characterId;
    @NotNull
    private Long benefitId;
}
