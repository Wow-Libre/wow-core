package com.register.wowlibre.infrastructure.repositories.notification_providers;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface NotificationProvidersRepository extends CrudRepository<NotificationProvidersEntity, Long> {

    Optional<NotificationProvidersEntity> findByName(String name);
}
