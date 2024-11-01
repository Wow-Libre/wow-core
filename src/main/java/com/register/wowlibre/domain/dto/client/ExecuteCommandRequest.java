package com.register.wowlibre.domain.dto.client;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@Getter
public class ExecuteCommandRequest {
    private String message;
    private byte[] salt;
}
