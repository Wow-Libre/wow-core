package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "benefit_guild")
public class BenefitGuildEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(
            name = "server_id",
            referencedColumnName = "id")
    @ManyToOne(
            optional = false,
            fetch = FetchType.EAGER)
    private ServerEntity serverId;
    @Column(name = "guild_name")
    private String guildName;
    @Column(name = "guild_id")
    private Long guildId;
    @Column(name = "benefit_id")
    private Long benefitId;
    private Boolean status;
}
