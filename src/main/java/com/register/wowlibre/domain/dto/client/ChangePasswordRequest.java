package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {
    private String password;
    @JsonProperty("account_id")
    private Long accountId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("expansion_id")
    private Integer expansionId;
    private byte[] salt;
}
