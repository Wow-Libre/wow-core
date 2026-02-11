package com.register.wowlibre.domain.dto;

import lombok.*;

import java.time.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionAdminDto {
    private Long id;
    private Long userId;
    private String referenceNumber;
    private String status;
    private String planName;
    private Double planPrice;
    private String currency;
    private String frequencyType;
    private Integer frequencyValue;
    private LocalDateTime activatedAt;
    private LocalDate expiresAt;
}
