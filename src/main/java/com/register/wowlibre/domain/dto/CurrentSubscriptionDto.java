package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentSubscriptionDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("reference_number")
    private String referenceNumber;
    @JsonProperty("status")
    private String status;
    @JsonProperty("plan_name")
    private String planName;
    @JsonProperty("plan_price")
    private Double planPrice;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("frequency_type")
    private String frequencyType;
    @JsonProperty("frequency_value")
    private Integer frequencyValue;
    @JsonProperty("activated_at")
    private LocalDateTime activatedAt;
    /**
     * Próxima renovación / fin del período facturado ({@code next_invoice_date} en BD).
     */
    @JsonProperty("renews_or_expires_at")
    private LocalDate renewsOrExpiresAt;
}
