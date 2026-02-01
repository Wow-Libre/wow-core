package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "trivia_question_rating", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "question_id", "user_id" })
})
public class TriviaQuestionRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** true = valoración positiva (👍), false = negativa (👎). Si negativa, se descuentan 2 puntos al creador. */
    @Column(name = "is_positive", nullable = false)
    private boolean isPositive;
}
