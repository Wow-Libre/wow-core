package com.register.wowlibre.domain.dto.interstitial;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterstitialAdminDto {
    private Long id;
    private String urlImg;
    private String redirectUrl;
    private boolean active;
    private long totalViews;
    private long uniqueViewers;
}
