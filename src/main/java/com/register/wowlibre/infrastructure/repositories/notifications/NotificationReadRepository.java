package com.register.wowlibre.infrastructure.repositories.notifications;

import com.register.wowlibre.infrastructure.entities.NotificationReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationReadRepository extends JpaRepository<NotificationReadEntity, Long> {

    List<NotificationReadEntity> findByUserId(Long userId);

    Optional<NotificationReadEntity> findByNotificationIdAndUserId(Long notificationId, Long userId);

    void deleteByNotificationId(Long notificationId);
}
