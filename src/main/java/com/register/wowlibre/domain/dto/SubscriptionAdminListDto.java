package com.register.wowlibre.domain.dto;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionAdminListDto {
    private long totalCount;
    private List<SubscriptionAdminDto> subscriptions;
}
