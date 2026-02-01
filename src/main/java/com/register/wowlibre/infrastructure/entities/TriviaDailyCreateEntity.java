package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "trivia_daily_create", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "user_id", "usage_date" })
})
public class TriviaDailyCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "count", nullable = false)
    private int count = 0;
}
