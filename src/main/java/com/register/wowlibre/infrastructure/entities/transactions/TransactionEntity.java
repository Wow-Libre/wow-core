package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

import java.io.*;
import java.time.*;

@Data
@Entity
@Table(name = "transaction")
public class TransactionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "realm_id")
    private Long realmId;
    private Double price;
    private String status;
    @JoinColumn(
            name = "subscription_id",
            referencedColumnName = "id")
    @ManyToOne(
            fetch = FetchType.EAGER)
    private SubscriptionEntity subscriptionId;
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id")
    @ManyToOne(
            fetch = FetchType.EAGER)
    private ProductEntity productId;
    @Column(name = "reference_number")
    private String referenceNumber;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "credit_points")
    private boolean creditPoints;
    private String currency;
    private boolean send;
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "is_subscription")
    private boolean isSubscription;
    @Column(name = "reference_payment")
    private String referencePayment;
}
