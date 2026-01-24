package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Data
@Entity
@Table(name = "subscription_benefit")
public class SubscriptionBenefitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "benefit_id")
    private Long benefitId;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "realm_id")
    private Long realmId;

}
