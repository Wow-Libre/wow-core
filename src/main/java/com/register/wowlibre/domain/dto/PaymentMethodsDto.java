package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.enums.*;

import java.time.*;

public record PaymentMethodsDto(Long id, PaymentType paymentType, String name, LocalDateTime createdAt) {
}
