package com.register.wowlibre.domain.model;

import lombok.*;

import java.time.*;

@Builder
public class ServerModel {
    public final Long id;
    public final String name;
    public final boolean status;
    public final String emulator;
    public final String avatar;
    public final String apiSecret;
    public final String version;
    public final String ip;
    public final LocalDateTime creationDate;
    public final String webSite;
    public final String password;
    public final String apiKey;
}
