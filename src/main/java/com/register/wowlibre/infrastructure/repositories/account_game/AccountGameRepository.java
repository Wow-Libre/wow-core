package com.register.wowlibre.infrastructure.repositories.account_game;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface AccountGameRepository extends CrudRepository<AccountGameEntity, Long> {
    Optional<AccountGameEntity> findByUserId_IdAndAccountIdAndStatusIsTrue(Long userId, Long accountId);

    Page<AccountGameEntity> findByUserId_IdAndStatusIsTrue(Long userId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AccountGameEntity a WHERE a.userId.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
