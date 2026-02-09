package com.register.wowlibre.domain.port.out.notifications;

import com.register.wowlibre.infrastructure.entities.NotificationEntity;

public interface ManageNotification {

    NotificationEntity save(NotificationEntity entity, String transactionId);

    void deleteById(Long id, String transactionId);

    /** Mark a notification as read for a user (idempotent). */
    void saveRead(Long notificationId, Long userId, String transactionId);
}
