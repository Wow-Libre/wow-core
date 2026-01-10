package com.register.wowlibre.domain.dto.interstitial;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class CreateInterstitial {
    @NotNull
    private String urlImg;
    @NotNull
    private String redirectUrl;
}
