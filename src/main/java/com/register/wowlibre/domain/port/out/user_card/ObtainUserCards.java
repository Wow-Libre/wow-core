package com.register.wowlibre.domain.port.out.user_card;

import com.register.wowlibre.infrastructure.entities.UserCardEntity;

import java.util.List;

public interface ObtainUserCards {

    List<UserCardEntity> findByUserId(Long userId, String transactionId);
}
