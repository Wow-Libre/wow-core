package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.*;
import java.time.*;

@Data
@Entity
@Table(name = "server")
public class ServerEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String emulator;
    private String avatar;
    private String expansion;
    private String ip;
    private String password;
    @Column(name = "api_key")
    private String apiKey;
    @Column(name = "api_secret")
    private String apiSecret;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    private String jwt;
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    @Column(name = "web_site")
    private String webSite;
    private boolean status;
}
