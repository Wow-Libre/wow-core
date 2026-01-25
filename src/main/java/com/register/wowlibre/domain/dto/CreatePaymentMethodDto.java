package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Data
public class CreatePaymentMethodDto {
    @NotNull
    private String paymentType;
    @NotNull
    private String name;
    @NotNull
    private Map<String, String> credentials;
}
