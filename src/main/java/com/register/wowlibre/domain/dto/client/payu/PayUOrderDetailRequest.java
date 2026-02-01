package com.register.wowlibre.domain.dto.client.payu;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayUOrderDetailRequest {
    private String language;
    private String command;
    private PayUMerchant merchant;
    private PayUDetails details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUMerchant {
        @JsonProperty("apiLogin")
        private String apiLogin;
        @JsonProperty("apiKey")
        private String apiKey;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUDetails {
        @JsonProperty("referenceCode")
        private String referenceCode;
    }
}
