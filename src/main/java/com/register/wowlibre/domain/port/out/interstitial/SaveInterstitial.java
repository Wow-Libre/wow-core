package com.register.wowlibre.domain.port.out.interstitial;

public interface SaveInterstitial {
    void saveInterstitial(String urlImg, String redirectUrl, String transactionId);

    void updateInterstitial(Long id, String urlImg, String redirectUrl, String transactionId);

    void delete(Long interstitialId, String transactionId);
}
