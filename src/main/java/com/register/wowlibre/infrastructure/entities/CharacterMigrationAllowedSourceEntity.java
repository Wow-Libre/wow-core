package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Orígenes de realmlist ({@code ginf.realmlist} en el dump) desde los que se aceptan migraciones.
 */
@Data
@Entity
@Table(
        name = "character_migration_allowed_source",
        schema = "platform",
        uniqueConstraints = @UniqueConstraint(name = "uq_cmas_realmlist_host", columnNames = "realmlist_host")
)
@NoArgsConstructor
public class CharacterMigrationAllowedSourceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Valor tal como viene en el dump (ej. {@code play.wowlibre.com}), sin protocolo.
     * La comparación en validación es sin distinguir mayúsculas.
     */
    @Column(name = "realmlist_host", nullable = false, length = 255)
    private String realmlistHost;

    @Column(name = "display_name", length = 120)
    private String displayName;

    @Column(nullable = false)
    private boolean active = true;

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
