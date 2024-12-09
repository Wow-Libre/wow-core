package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Data
@Entity
@Table(name = "user_server_coins")
public class MachineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "server_id")
    private Long serverId;
    private Integer coint;
    @Column(name = "last_win")
    private LocalDateTime lastWin;
}
