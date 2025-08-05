package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class CreateNotificationProviderDto {
    @NotNull
    private String name;
    @NotNull
    private String host;
    @NotNull
    private String client;
    @NotNull
    private String secret;
}
