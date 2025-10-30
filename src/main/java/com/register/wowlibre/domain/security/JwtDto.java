package com.register.wowlibre.domain.security;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class JwtDto {
    public Long id;
    public String jwt;
    @JsonProperty("refresh_token")
    public String refreshToken;
    @JsonProperty("expiration_date")
    public Date expirationDate;
    @JsonProperty("avatar_url")
    public String avatarUrl;
    public String language;
    @JsonProperty("pending_validation")
    public boolean pendingValidation;
    public boolean isAdmin;

    public JwtDto(Long userId, String jwt, String refreshToken, Date expirationDate, String avatarUrl, String language,
                  boolean pendingValidation, boolean isAdmin) {
        this.id = userId;
        this.jwt = jwt;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
        this.avatarUrl = avatarUrl;
        this.language = language;
        this.pendingValidation = pendingValidation;
        this.isAdmin = isAdmin;
    }
}
