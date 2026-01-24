package com.register.wowlibre.infrastructure.entities.transactions;

import com.wow.libre.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Data
@Entity
@Table(name = "payment_gateways")
public class PaymentGatewaysEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
