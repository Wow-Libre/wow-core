package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "card_catalog", schema = "platform")
public class CardCatalogEntity {

    @Id
    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;

    @Column(name = "display_name", length = 128)
    private String displayName;

    /** Probabilidad de salir en un sobre (1-100). */
    @Column(name = "probability", nullable = false)
    private Integer probability = 50;

    /** Si está activa aparece en catálogo y en sorteos de sobres. */
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
