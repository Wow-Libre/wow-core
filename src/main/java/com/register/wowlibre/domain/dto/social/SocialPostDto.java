package com.register.wowlibre.domain.dto.social;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialPostDto {
    private Long id;
    private Long userId;
    private String authorUsername;
    private String authorAvatar;
    private boolean authorPremium;
    private String content;
    private List<String> mediaUrls;
    private long likesCount;
    private long commentsCount;
    private boolean likedByMe;
    private LocalDateTime createdAt;
}
