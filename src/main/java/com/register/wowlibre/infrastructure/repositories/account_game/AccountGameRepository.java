package com.register.wowlibre.infrastructure.repositories.account_game;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface AccountGameRepository extends CrudRepository<AccountGameEntity, Long> {
    Optional<AccountGameEntity> findByUserId_IdAndAccountIdAndServerId_idAndStatusIsTrue(Long userId, Long accountId,
                                                                                      Long serverId);

    Page<AccountGameEntity> findByUserId_IdAndStatusIsTrue(Long userId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AccountGameEntity a WHERE a.userId.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM AccountGameEntity a " +
            "INNER JOIN a.serverId s " +
            "INNER JOIN a.userId us " +
            "WHERE s.name LIKE %:serverName% " +
            "AND us.id = :userId " +
            "AND a.status = true " +
            "AND a.username LIKE %:username%")
    Page<AccountGameEntity> findByUserId_IdAndStatusIsTrueAndServerNameAndUsername(
            @Param("serverName") String serverName,
            @Param("userId") Long userId,
            @Param("username") String username,
            Pageable pageable);

    List<AccountGameEntity> findByUserId_IdAndServerId_IdAndStatusIsTrue(Long userId, Long serverId);

}
