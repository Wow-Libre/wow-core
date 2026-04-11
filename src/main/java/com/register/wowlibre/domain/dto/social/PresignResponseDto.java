package com.register.wowlibre.domain.dto.social;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignResponseDto {
    private String uploadUrl;
    private String publicUrl;
    private String key;
    private int expiresInSeconds;
}
