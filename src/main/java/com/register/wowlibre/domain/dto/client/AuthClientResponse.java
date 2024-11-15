package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

@Data
public class AuthClientResponse {

    private String jwt;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expiration_date")
    private Date expirationDate;

    public AuthClientResponse(String jwt, String refreshToken, Date expirationDate) {
        this.jwt = jwt;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }
}
