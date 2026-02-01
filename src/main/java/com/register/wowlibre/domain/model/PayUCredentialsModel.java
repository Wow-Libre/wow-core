package com.register.wowlibre.domain.model;

public record PayUCredentialsModel(String accountId, String merchantId, String signature, String test) {
}
