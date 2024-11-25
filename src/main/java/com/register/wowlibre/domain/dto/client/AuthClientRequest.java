package com.register.wowlibre.domain.dto.client;

import lombok.*;

@AllArgsConstructor
@Getter
public class AuthClientRequest {
    private String username;

    private String password;
}
