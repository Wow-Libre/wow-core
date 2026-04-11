package com.register.wowlibre.domain.dto.social;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeToggleResponseDto {
    private boolean liked;
    private long likesCount;
}
