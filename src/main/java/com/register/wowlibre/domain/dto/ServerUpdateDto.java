package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.*;

@Data
public class ServerUpdateDto {
    @NotNull
    @Length(min = 5, max = 30)
    private String name;
    @NotNull
    private String webSite;
    @NotNull
    @Length(min = 2, max = 50)
    private String ip;
    @NotNull
    @Length(min = 2, max = 20)
    private String password;
    @NotNull
    @Length(min = 2, max = 20)
    private String oldPassword;
}
