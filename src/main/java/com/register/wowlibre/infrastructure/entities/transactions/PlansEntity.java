package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Data
@Entity
@Table(name = "plan", schema = "transactions")
public class PlansEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String name;
    private Double price;
    @Column(name = "price_title")
    private String priceTitle;
    private String description;
    private Integer discount;
    private boolean status;
    private String currency;
    @Column(name = "frequency_type")
    private String frequencyType;
    @Column(name = "frequency_value")
    private Integer frequencyValue;
    @Column(name = "free_trial_days")
    private Integer freeTrialDays;
    @Column(name = "tax")
    private String tax;
    @Column(name = "return_tax")
    private String returnTax;
    @Column(name = "features", columnDefinition = "JSON")
    private String features;
    private String language;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
