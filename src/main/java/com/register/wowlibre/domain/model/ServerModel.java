package com.register.wowlibre.domain.model;

import lombok.*;

import java.time.*;

@Builder
public class ServerModel {
    public Long id;
    public String name;
    public boolean status;
    public String emulator;
    public String avatar;
    public String apiSecret;
    public String version;
    public String ip;
    public LocalDateTime creationDate;
    public String webSite;
    public String password;
    public String apiKey;
}
