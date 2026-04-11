package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications", schema = "platform")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    /**
     * Si es null, la notificación es global (la ven todos los usuarios, comportamiento histórico).
     * Si está informado, solo ese usuario la verá en su bandeja.
     */
    @Column(name = "recipient_user_id")
    private Long recipientUserId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
