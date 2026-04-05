package com.register.wowlibre.infrastructure.entities;

import com.register.wowlibre.domain.enums.CharacterMigrationStagingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "character_migration_staging", schema = "platform")
@NoArgsConstructor
public class CharacterMigrationStagingEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "realm_id", nullable = false)
    private RealmEntity realm;

    @Column(name = "character_name", length = 80)
    private String characterName;

    @Column(name = "character_guid", length = 50)
    private String characterGuid;

    /** Nombre de usuario deseado para la cuenta de juego en el reino destino; la contraseña la genera el backend al aprobar. */
    @Column(name = "target_game_account_username", length = 20)
    private String targetGameAccountUsername;

    @Column(name = "raw_data", columnDefinition = "JSON", nullable = false)
    private String rawData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CharacterMigrationStagingStatus status = CharacterMigrationStagingStatus.PENDING;

    @Column(name = "validation_errors", columnDefinition = "TEXT")
    private String validationErrors;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
