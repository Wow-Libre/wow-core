package com.register.wowlibre.domain.dto.interstitial;

import lombok.*;

@Data
@AllArgsConstructor
public class InterstitialDto {
    private Long id;
    private String urlImg;
    private String redirectUrl;
    private boolean active;
}
