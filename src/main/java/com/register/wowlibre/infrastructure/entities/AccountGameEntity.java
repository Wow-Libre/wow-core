package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "account_game")
public class AccountGameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    private boolean status;
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id")
    @ManyToOne(
            optional = false,
            fetch = FetchType.EAGER)
    private UserEntity userId;
    @JoinColumn(
            name = "server_id",
            referencedColumnName = "id")
    @ManyToOne(
            optional = false,
            fetch = FetchType.EAGER)
    private ServerEntity serverId;

}
