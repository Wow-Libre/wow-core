package com.register.wowlibre.domain.dto.client;

import lombok.*;

@AllArgsConstructor
@Getter
public class AccountUpdateMailRequest {
    private String mail;
    private String username;
}
