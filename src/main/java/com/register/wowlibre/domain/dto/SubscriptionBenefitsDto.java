package com.register.wowlibre.domain.dto;

import lombok.*;

import java.util.*;

@AllArgsConstructor
@Getter
public class SubscriptionBenefitsDto {
    private List<BenefitsPremiumDto> benefits;
    private int size;
}
