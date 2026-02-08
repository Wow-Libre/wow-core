package com.register.wowlibre.domain.dto.interstitial;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class CreateInterstitial {
    @NotNull
    @JsonProperty("urlImg")
    private String urlImg;
    @NotNull
    @JsonProperty("redirectUrl")
    private String redirectUrl;
}
