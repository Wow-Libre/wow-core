package com.register.wowlibre.domain.port.out.wallet;

import com.register.wowlibre.infrastructure.entities.transactions.*;

import java.util.*;

public interface ObtainWallet {
    Optional<WalletsEntity> findByUserId(Long userId);
}
