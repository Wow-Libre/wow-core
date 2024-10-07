package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Builder
public class AccountGameCreateDto {
    public String username;
    public String password;
    public String email;
    @JsonProperty("user_id")
    public Long userId;
    public String expansion;
    public byte[] salt;
}
