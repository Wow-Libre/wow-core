package com.register.wowlibre.infrastructure.repositories.wallets;

import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface WalletsRepository extends CrudRepository<WalletsEntity, Long> {
    Optional<WalletsEntity> findByUserId_id(Long userId);
}
