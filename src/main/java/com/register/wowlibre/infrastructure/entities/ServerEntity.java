package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Data
@Entity
@Table(name = "server")
public class ServerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String emulator;
    private String avatar;
    private String version;
    private String ip;
    private String password;
    @Column(name = "api_key")
    private String apiKey;
    @Column(name = "api_secret")
    private String apiSecret;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "web_site")
    private String webSite;
    private boolean status;
}
