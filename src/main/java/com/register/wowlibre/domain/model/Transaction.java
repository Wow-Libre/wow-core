package com.register.wowlibre.domain.model;

import lombok.*;

import java.time.*;

@AllArgsConstructor
@Data
public class Transaction {
    private Long id;
    private Double price;
    private String currency;
    private String status;
    private Integer progress;
    private LocalDateTime date;
    private String referenceNumber;
    private String productName;
    private String logo;
}
