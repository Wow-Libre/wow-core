package com.register.wowlibre.domain.port.out.user;

import com.register.wowlibre.infrastructure.entities.*;

public interface SaveUserPort {
    UserEntity save(UserEntity user, String email);
}
