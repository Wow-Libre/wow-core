package com.register.wowlibre.infrastructure.entities.social;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "social_post_like", schema = "platform",
        uniqueConstraints = @UniqueConstraint(name = "uk_social_like_post_user", columnNames = {"post_id", "user_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialPostLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
