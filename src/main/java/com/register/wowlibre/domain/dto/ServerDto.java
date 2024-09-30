package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.*;

@Data
public class ServerDto {
    @NotNull
    @Length(min = 5, max = 30)
    private String name;
    @NotNull
    @Length(min = 5, max = 30)
    private String emulator;
    @NotNull
    @Length(min = 2, max = 10)
    private String version;
    @NotNull
    private String webSite;
    @NotNull
    @Length(min = 2, max = 50)
    private String ip;
    @NotNull
    @Length(min = 2, max = 20)
    private String password;
}
