package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "battle_pass_reward", schema = "platform")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattlePassRewardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season_id", nullable = false)
    private Long seasonId;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "core_item_id", nullable = false)
    private Integer coreItemId;

    @Column(name = "wowhead_id")
    private Integer wowheadId;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
