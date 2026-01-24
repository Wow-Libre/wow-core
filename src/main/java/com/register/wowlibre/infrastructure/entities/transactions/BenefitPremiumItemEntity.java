package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "benefit_premium_item")
public class BenefitPremiumItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "benefit_premium_id", referencedColumnName = "id", nullable = false)
    private BenefitPremiumEntity benefitPremium;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}

