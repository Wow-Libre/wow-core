package com.register.wowlibre.domain.port.out.notification_provider;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface ObtainNotificationProvider {
    Optional<NotificationProvidersEntity> findByName(String name, String transactionId);
}
