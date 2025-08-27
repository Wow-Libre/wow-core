package com.register.wowlibre.domain.port.out.notification_provider;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainNotificationProvider {
    Optional<NotificationProvidersEntity> findByType(String name, String transactionId);

    Optional<NotificationProvidersEntity> findById(Long id, String transactionId);

    List<NotificationProvidersEntity> findAll(String transactionId);
}
