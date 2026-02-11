package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "notification_read", schema = "platform",
       uniqueConstraints = @UniqueConstraint(columnNames = { "notification_id", "user_id" }))
public class NotificationReadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
