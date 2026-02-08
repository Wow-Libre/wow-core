package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@Getter
public class AuthClientCreateRequest {
    private String username;
    private String password;
    @JsonProperty("realm_id")
    private Long realmId;
    private String emulator;
    @JsonProperty("expansion_id")
    private Integer expansionId;
}
