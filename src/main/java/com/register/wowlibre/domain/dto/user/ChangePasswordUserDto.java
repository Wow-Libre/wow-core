package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.*;

@Data
public class ChangePasswordUserDto {
    @Length(min = 5, max = 30)
    private String password;
    @NotNull
    @NotEmpty
    @Length(min = 5, max = 30)
    private String newPassword;
}
