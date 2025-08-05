package com.register.wowlibre.domain.port.in.notification_provider;

import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface NotificationProviderPort {
    /**
     * Retrieves a notification provider by its name.
     *
     * @param name the name of the notification provider
     * @return an Optional containing the notification provider if found, otherwise empty
     */
    Optional<NotificationProvidersEntity> findByName(String name, String transactionId);

    /**
     * Saves a notification provider.
     *
     * @param notificationProvider the notification provider to save
     * @param transactionId        the transaction ID for tracking
     */
    void save(NotificationProvidersEntity notificationProvider, String transactionId);

}
