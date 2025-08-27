package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.*;

@Data
public class CreateNotificationProviderDto {
    @NotNull
    @Length(min = 2, max = 50)
    private String name;
    @NotNull
    @Length(min = 5, max = 50)
    private String host;
    @NotNull
    @Length(min = 5, max = 50)
    private String client;
    @NotNull
    @Length(min = 5, max = 50)
    private String secret;
}
