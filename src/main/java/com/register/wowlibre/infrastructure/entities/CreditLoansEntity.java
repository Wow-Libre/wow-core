package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Data
@Entity
@Table(name = "credit_loans")
public class CreditLoansEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "reference_serial")
    private String referenceSerial;
    @Column(name = "character_id")
    private Long characterId;
    @Column(name = "server_id")
    private Long serverId;
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id")
    @ManyToOne(
            optional = false,
            fetch = FetchType.EAGER)
    private UserEntity userId;
    private Double amountTransferred;
    private Double debtToPay;
    private LocalDateTime transactionDate;
    private LocalDateTime paymentDate;
    private Integer interests;
    private boolean send;
    private boolean status;
}
