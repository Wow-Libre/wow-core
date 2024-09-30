package com.register.wowlibre.infrastructure.repositories.account_game;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.*;

public interface AccountGameRepository extends CrudRepository<AccountGameEntity, Long> {

    Page<AccountGameEntity> findByUserId_IdAndStatusIsTrue(Long userId, Pageable pageable);
}
