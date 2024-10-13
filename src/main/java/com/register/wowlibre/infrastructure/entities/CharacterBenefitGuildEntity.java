package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "character_benefit_guild")
public class CharacterBenefitGuildEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "character_id")
    private Long characterId;
    @Column(name = "account_id")
    private Long accountId;
    @JoinColumn(
            name = "benefit_guild_id",
            referencedColumnName = "id")
    @ManyToOne(
            optional = false,
            fetch = FetchType.EAGER)
    private BenefitGuildEntity benefitGuildId;
}
