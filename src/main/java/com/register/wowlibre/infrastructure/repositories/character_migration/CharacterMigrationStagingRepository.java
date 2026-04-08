package com.register.wowlibre.infrastructure.repositories.character_migration;

import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
import com.register.wowlibre.infrastructure.entities.CharacterMigrationStagingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CharacterMigrationStagingRepository extends JpaRepository<CharacterMigrationStagingEntity, Long> {

    boolean existsByUserIdAndStatus(Long userId, CharacterMigrationStagingStatus status);

    long countByUserIdAndStatus(Long userId, CharacterMigrationStagingStatus status);

    List<CharacterMigrationStagingEntity> findByRealm_IdOrderByCreatedAtDesc(Long realmId);

    Optional<CharacterMigrationStagingEntity> findByIdAndRealm_Id(Long id, Long realmId);

    @Query(
            "SELECT e FROM CharacterMigrationStagingEntity e JOIN FETCH e.realm WHERE e.userId = :userId"
                    + " ORDER BY e.createdAt DESC")
    List<CharacterMigrationStagingEntity> findAllByUserIdWithRealmOrderByCreatedAtDesc(
            @Param("userId") Long userId);

    @Query(
            "SELECT e FROM CharacterMigrationStagingEntity e JOIN FETCH e.realm WHERE e.userId = :userId"
                    + " AND e.realm.id = :realmId ORDER BY e.createdAt DESC")
    List<CharacterMigrationStagingEntity> findByRealmIdAndUserIdWithRealmOrderByCreatedAtDesc(
            @Param("realmId") Long realmId, @Param("userId") Long userId);
}
