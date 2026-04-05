package com.register.wowlibre.infrastructure.repositories.character_migration;

import com.register.wowlibre.infrastructure.entities.CharacterMigrationAllowedSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharacterMigrationAllowedSourceRepository
        extends JpaRepository<CharacterMigrationAllowedSourceEntity, Long> {

    long countByActiveTrue();

    List<CharacterMigrationAllowedSourceEntity> findAllByActiveTrueOrderByRealmlistHostAsc();

    Optional<CharacterMigrationAllowedSourceEntity> findByIdAndActiveTrue(Long id);
}
