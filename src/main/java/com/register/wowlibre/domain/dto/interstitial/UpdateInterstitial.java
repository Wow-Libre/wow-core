package com.register.wowlibre.domain.dto.interstitial;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateInterstitial {
    @NotNull
    private Long id;
    @NotNull
    private String urlImg;
    @NotNull
    private String redirectUrl;
}
