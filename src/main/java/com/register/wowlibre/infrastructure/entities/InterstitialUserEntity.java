package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Data
@Entity
@Table(name = "interstitial_user")
public class InterstitialUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    private Long views;
    @ManyToOne
    @JoinColumn(name = "interstitial_id", nullable = false)
    private InterstitialEntity interstitialId;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
