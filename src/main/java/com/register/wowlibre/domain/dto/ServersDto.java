package com.register.wowlibre.domain.dto;

import lombok.*;

import java.time.*;

@Data
public class ServersDto {
    public Long id;
    public String name;
    public boolean status;
    public String emulator;
    public String avatar;
    public String expansion;
    public LocalDateTime creationDate;
    public String webSite;
    public String expName;
}
