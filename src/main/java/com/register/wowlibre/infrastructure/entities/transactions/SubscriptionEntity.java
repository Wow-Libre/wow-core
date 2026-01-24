package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Data
@Entity
@Table(name = "subscription")
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long userId;
    @JoinColumn(
            name = "plan_id",
            referencedColumnName = "id")
    @ManyToOne(
            optional = false,
            fetch = FetchType.EAGER)
    private PlanEntity planId;
    @Column(name = "reference_number")
    private String referenceNumber;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "next_invoice_date")
    private LocalDateTime nextInvoiceDate;
    private String status;
}
