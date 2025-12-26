package com.register.wowlibre.infrastructure.repositories.account_game;

import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface AccountGameRepository extends CrudRepository<AccountGameEntity, Long> {
    Optional<AccountGameEntity> findByUserId_IdAndAccountIdAndRealmId_idAndStatusIsTrue(Long userId, Long accountId,
                                                                                        Long realmId);

    Optional<AccountGameEntity> findByUserId_IdAndAccountIdAndStatusIsTrue(Long userId, Long accountId);

    Optional<AccountGameEntity> findByAccountIdAndStatusIsTrue(Long accountId);

    Page<AccountGameEntity> findByUserId_IdAndStatusIsTrue(Long userId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AccountGameEntity a WHERE a.userId.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM AccountGameEntity a " +
            "INNER JOIN a.realmId s " +
            "INNER JOIN a.userId us " +
            "WHERE s.name LIKE %:realName% " +
            "AND us.id = :userId " +
            "AND a.status = true " +
            "AND a.username LIKE %:username%")
    Page<AccountGameEntity> findByUserId_IdAndStatusIsTrueAndRealmNameAndUsername(
            @Param("realName") String realName,
            @Param("userId") Long userId,
            @Param("username") String username,
            Pageable pageable);

    List<AccountGameEntity> findByUserId_IdAndRealmId_IdAndStatusIsTrue(Long userId, Long serverId);

    Optional<AccountGameEntity> findByIdAndUserId_Id(Long id, Long userId);

    @Query("SELECT COUNT(a) FROM AccountGameEntity a WHERE a.userId.id = :userId AND a.status = true")
    long countActiveAccountsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT a.realmId.id) FROM AccountGameEntity a WHERE a.userId.id = :userId AND a.status = true")
    long countDistinctRealmsByUserId(@Param("userId") Long userId);
}
