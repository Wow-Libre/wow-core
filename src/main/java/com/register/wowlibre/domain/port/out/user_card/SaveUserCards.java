package com.register.wowlibre.domain.port.out.user_card;

import com.register.wowlibre.infrastructure.entities.UserCardEntity;

import java.util.List;

/**
 * Puerto para persistir cartas obtenidas por el usuario.
 */
public interface SaveUserCards {

    void saveAll(List<UserCardEntity> entities);

    /**
     * Añade o incrementa cantidad por cada código (para compra de sobre o recepción de carta).
     */
    void addOrIncrement(Long userId, List<String> cardCodes);
}
