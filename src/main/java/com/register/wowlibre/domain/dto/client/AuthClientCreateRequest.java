package com.register.wowlibre.domain.dto.client;

import lombok.*;

@AllArgsConstructor
@Getter
public class AuthClientCreateRequest {
    private String username;
    private String password;
    private byte[] salt;
}
