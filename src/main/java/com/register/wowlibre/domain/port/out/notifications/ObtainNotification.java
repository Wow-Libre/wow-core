package com.register.wowlibre.domain.port.out.notifications;

import com.register.wowlibre.infrastructure.entities.NotificationEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ObtainNotification {

    List<NotificationEntity> findAllOrderByCreatedAtDesc(String transactionId);

    /**
     * Notificaciones que debe ver un usuario: globales (sin destinatario) + las dirigidas a él.
     */
    List<NotificationEntity> findVisibleForUser(Long userId, String transactionId);

    /** IDs of notifications that this user has marked as read. */
    Set<Long> findReadNotificationIdsByUserId(Long userId, String transactionId);

    Optional<NotificationEntity> findById(Long id, String transactionId);
}
