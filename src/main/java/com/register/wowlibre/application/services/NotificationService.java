package com.register.wowlibre.application.services;

import com.register.wowlibre.domain.dto.notifications.NotificationAdminDto;
import com.register.wowlibre.domain.dto.notifications.NotificationDto;
import com.register.wowlibre.domain.dto.notifications.NotificationRequestDto;
import com.register.wowlibre.domain.port.in.notifications.NotificationPort;
import com.register.wowlibre.domain.port.out.notifications.ManageNotification;
import com.register.wowlibre.domain.port.out.notifications.ObtainNotification;
import com.register.wowlibre.infrastructure.entities.NotificationEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService implements NotificationPort {

    private final ObtainNotification obtainNotification;
    private final ManageNotification manageNotification;

    public NotificationService(ObtainNotification obtainNotification, ManageNotification manageNotification) {
        this.obtainNotification = obtainNotification;
        this.manageNotification = manageNotification;
    }

    @Override
    public List<NotificationDto> findByUserId(Long userId, boolean unreadOnly, String transactionId) {
        List<NotificationEntity> all = obtainNotification.findAllOrderByCreatedAtDesc(transactionId);
        Set<Long> readIds = obtainNotification.findReadNotificationIdsByUserId(userId, transactionId);
        List<NotificationEntity> list = unreadOnly
                ? all.stream().filter(n -> !readIds.contains(n.getId())).toList()
                : all;
        return list.stream()
                .map(n -> toDto(n, readIds.contains(n.getId())))
                .toList();
    }

    @Override
    public void markAsRead(Long notificationId, Long userId, String transactionId) {
        obtainNotification.findById(notificationId, transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        manageNotification.saveRead(notificationId, userId, transactionId);
    }

    @Override
    public void markAllAsRead(Long userId, String transactionId) {
        List<NotificationEntity> all = obtainNotification.findAllOrderByCreatedAtDesc(transactionId);
        Set<Long> readIds = obtainNotification.findReadNotificationIdsByUserId(userId, transactionId);
        List<Long> unreadIds = all.stream()
                .map(NotificationEntity::getId)
                .filter(id -> !readIds.contains(id))
                .collect(Collectors.toList());
        for (Long id : unreadIds) {
            manageNotification.saveRead(id, userId, transactionId);
        }
    }

    @Override
    public List<NotificationAdminDto> findAllForAdmin(String transactionId) {
        return obtainNotification.findAllOrderByCreatedAtDesc(transactionId).stream()
                .map(this::toAdminDto)
                .toList();
    }

    @Override
    public NotificationAdminDto create(NotificationRequestDto request, String transactionId) {
        NotificationEntity entity = new NotificationEntity();
        entity.setTitle(request.getTitle());
        entity.setMessage(request.getMessage() != null ? request.getMessage() : "");
        NotificationEntity saved = manageNotification.save(entity, transactionId);
        return toAdminDto(saved);
    }

    @Override
    public NotificationAdminDto update(NotificationRequestDto request, String transactionId) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        NotificationEntity existing = obtainNotification.findById(request.getId(), transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + request.getId()));
        existing.setTitle(request.getTitle());
        existing.setMessage(request.getMessage() != null ? request.getMessage() : "");
        NotificationEntity saved = manageNotification.save(existing, transactionId);
        return toAdminDto(saved);
    }

    @Override
    public void deleteById(Long id, String transactionId) {
        if (!obtainNotification.findById(id, transactionId).isPresent()) {
            throw new IllegalArgumentException("Notification not found: " + id);
        }
        manageNotification.deleteById(id, transactionId);
    }

    private NotificationDto toDto(NotificationEntity e, boolean read) {
        return new NotificationDto(e.getId(), e.getTitle(), e.getMessage(), read, e.getCreatedAt());
    }

    private NotificationAdminDto toAdminDto(NotificationEntity e) {
        return new NotificationAdminDto(e.getId(), e.getTitle(), e.getMessage(), e.getCreatedAt());
    }
}
