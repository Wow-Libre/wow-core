package com.register.wowlibre.domain.port.out.notification_provider;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveNotificationProvider {
    void save(NotificationProvidersEntity notificationProvider, String transactionId);
}
