package com.register.wowlibre.application.services;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.notification_provider.*;
import com.register.wowlibre.domain.port.out.notification_provider.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class NotificationProviderService implements NotificationProviderPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationProviderService.class);

    private final ObtainNotificationProvider obtainNotificationProvider;
    private final SaveNotificationProvider saveNotificationProvider;

    public NotificationProviderService(ObtainNotificationProvider obtainNotificationProvider,
                                       SaveNotificationProvider saveNotificationProvider) {
        this.obtainNotificationProvider = obtainNotificationProvider;
        this.saveNotificationProvider = saveNotificationProvider;
    }

    @Override
    public Optional<NotificationProvidersEntity> findByName(String name, String transactionId) {
        return obtainNotificationProvider.findByType(name, transactionId);
    }

    @Override
    public void save(NotificationProvidersEntity notificationProvider, String transactionId) {
        saveNotificationProvider.save(notificationProvider, transactionId);
    }

    public void configProvider(CreateNotificationProviderDto request,
                               String transactionId) {


        NotificationType notificationType = NotificationType.fromName(request.getName());

        if (notificationType == null) {
            LOGGER.error("The notification type is not valid. TransactionId: {}", transactionId);
            throw new InternalException("The notification type is not valid.", transactionId);
        }

        Optional<NotificationProvidersEntity> provider = findByName(notificationType.name(),
                transactionId);

        if (provider.isPresent()) {
            LOGGER.error("The notification provider with this name already exists. TransactionId: {}", transactionId);
            throw new InternalException("The notification provider with this name already exists.",
                    transactionId);
        }

        NotificationProvidersEntity providersEntity = new NotificationProvidersEntity();
        providersEntity.setType(notificationType.name());
        providersEntity.setHost(request.getHost());
        providersEntity.setClient(request.getClient());
        providersEntity.setSecretKey(request.getSecret());
        save(providersEntity, transactionId);
    }

    @Override
    public void deleteProvider(Long providerId, String transactionId) {

        obtainNotificationProvider.findById(providerId, transactionId)
                .ifPresentOrElse(
                        provider -> saveNotificationProvider.delete(provider, transactionId),
                        () -> {
                            LOGGER.error("The notification provider with this id does not exist. TransactionId: {}",
                                    transactionId);
                            throw new InternalException("The notification provider with this id does not exist.",
                                    transactionId);
                        }
                );

    }

    @Override
    public List<NotificationProviderModel> allProviders(String transactionId) {

        return obtainNotificationProvider.findAll(transactionId).stream()
                .map(provider -> new NotificationProviderModel(provider.getId(), provider.getType(),
                        provider.getClient(), provider.getHost(), provider.getClient(), provider.getSecretKey()))
                .toList();
    }
}
