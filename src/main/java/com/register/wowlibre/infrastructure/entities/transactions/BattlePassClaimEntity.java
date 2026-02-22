package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "battle_pass_claim", schema = "platform")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattlePassClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season_id", nullable = false)
    private Long seasonId;

    @Column(name = "realm_id", nullable = false)
    private Long realmId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "character_id", nullable = false)
    private Long characterId;

    @Column(name = "reward_id", nullable = false)
    private Long rewardId;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;
}
