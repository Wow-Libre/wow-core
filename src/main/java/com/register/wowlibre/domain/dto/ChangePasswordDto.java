package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ChangePasswordDto {
    @NotNull
    private String newPassword;
    @NotNull
    private String password;
    @NotNull
    private Long accountId;
    @NotNull
    private Long serverId;
}
