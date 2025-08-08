package com.register.wowlibre.domain.port.in.notification_provider;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.infrastructure.entities.*;

import java.util.*;

public interface NotificationProviderPort {
    /**
     * Retrieves a notification provider by its name.
     *
     * @param name          the name of the notification provider
     * @param transactionId the transaction ID for tracking
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

    /**
     * Configures a notification provider.
     *
     * @param request       details for creating the notification provider
     * @param transactionId the transaction ID for tracking
     */
    void configProvider(CreateNotificationProviderDto request, String transactionId);

    /**
     * Deletes a notification provider.
     *
     * @param providerId    Identifier of the provider to delete
     * @param transactionId the transaction ID for tracking
     */
    void deleteProvider(Long providerId, String transactionId);

    /**
     * Retrieves all notification providers for a user.
     *
     * @param transactionId the transaction ID for tracking
     * @return a list of notification providers
     */
    List<NotificationProviderModel> allProviders(String transactionId);
}
