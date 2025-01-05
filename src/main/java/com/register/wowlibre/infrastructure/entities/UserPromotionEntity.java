package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Data
@Entity
@Table(name = "user_promotion")
public class UserPromotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "character_id")
    private Long characterId;
    @Column(name = "promotion_id")
    private Long promotionId;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "server_id")
    private Long serverId;
}
