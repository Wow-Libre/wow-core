package com.register.wowlibre.domain.port.in.notifications;

import com.register.wowlibre.domain.dto.notifications.NotificationAdminDto;
import com.register.wowlibre.domain.dto.notifications.NotificationDto;
import com.register.wowlibre.domain.dto.notifications.NotificationRequestDto;

import java.util.List;

public interface NotificationPort {

    /** List notifications for the authenticated user (unread only for "active" list, or all). */
    List<NotificationDto> findByUserId(Long userId, boolean unreadOnly, String transactionId);

    /** Mark a notification as read (only if it belongs to the user). */
    void markAsRead(Long notificationId, Long userId, String transactionId);

    /** Mark all notifications of a user as read. */
    void markAllAsRead(Long userId, String transactionId);

    /** Admin: list all notifications. */
    List<NotificationAdminDto> findAllForAdmin(String transactionId);

    /** Admin: create notification. */
    NotificationAdminDto create(NotificationRequestDto request, String transactionId);

    /** Admin: update notification. */
    NotificationAdminDto update(NotificationRequestDto request, String transactionId);

    /** Admin: delete notification. */
    void deleteById(Long id, String transactionId);
}
