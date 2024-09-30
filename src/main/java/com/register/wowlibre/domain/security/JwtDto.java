package com.register.wowlibre.domain.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class JwtDto {

    public String jwt;
    @JsonProperty("refresh_token")
    public String refreshToken;
    @JsonProperty("expiration_date")
    public Date expirationDate;
    @JsonProperty("avatar_url")
    public String avatarUrl;

    public String language;

    public JwtDto(String jwt, String refreshToken, Date expirationDate, String avatarUrl, String language) {
        this.jwt = jwt;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
        this.avatarUrl = avatarUrl;
        this.language = language;
    }
}
