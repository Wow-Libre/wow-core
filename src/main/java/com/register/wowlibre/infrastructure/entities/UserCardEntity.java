package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_cards", schema = "platform",
       uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "card_code" }))
public class UserCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "card_code", nullable = false, length = 32)
    private String cardCode;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "obtained_at", updatable = false)
    private LocalDateTime obtainedAt;

    @PrePersist
    protected void onCreate() {
        if (obtainedAt == null) {
            obtainedAt = LocalDateTime.now();
        }
    }
}
