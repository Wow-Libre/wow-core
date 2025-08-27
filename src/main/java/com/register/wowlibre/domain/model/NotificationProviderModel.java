package com.register.wowlibre.domain.model;

import lombok.*;

@AllArgsConstructor
@Data
public class NotificationProviderModel {
    private Long id;
    private String name;
    private String client;
    private String host;
    private String createdAt;
    private String updatedAt;
}
