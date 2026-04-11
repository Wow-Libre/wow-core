package com.register.wowlibre.domain.dto.social;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialCommentDto {
    private Long id;
    private Long userId;
    private String authorUsername;
    private String authorAvatar;
    private String content;
    private LocalDateTime createdAt;
}
