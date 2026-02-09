package com.register.wowlibre.infrastructure.repositories.notifications;

import com.register.wowlibre.domain.port.out.notifications.ManageNotification;
import com.register.wowlibre.domain.port.out.notifications.ObtainNotification;
import com.register.wowlibre.infrastructure.entities.NotificationEntity;
import com.register.wowlibre.infrastructure.entities.NotificationReadEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaNotificationAdapter implements ObtainNotification, ManageNotification {

    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;

    public JpaNotificationAdapter(NotificationRepository notificationRepository,
                                  NotificationReadRepository notificationReadRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationReadRepository = notificationReadRepository;
    }

    @Override
    public List<NotificationEntity> findAllOrderByCreatedAtDesc(String transactionId) {
        return notificationRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public Set<Long> findReadNotificationIdsByUserId(Long userId, String transactionId) {
        return notificationReadRepository.findByUserId(userId).stream()
                .map(NotificationReadEntity::getNotificationId)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<NotificationEntity> findById(Long id, String transactionId) {
        return notificationRepository.findById(id);
    }

    @Override
    public NotificationEntity save(NotificationEntity entity, String transactionId) {
        return notificationRepository.save(entity);
    }

    @Override
    public void deleteById(Long id, String transactionId) {
        notificationReadRepository.deleteByNotificationId(id);
        notificationRepository.deleteById(id);
    }

    @Override
    public void saveRead(Long notificationId, Long userId, String transactionId) {
        if (notificationReadRepository.findByNotificationIdAndUserId(notificationId, userId).isEmpty()) {
            NotificationReadEntity r = new NotificationReadEntity();
            r.setNotificationId(notificationId);
            r.setUserId(userId);
            notificationReadRepository.save(r);
        }
    }
}
