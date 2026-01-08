package com.register.wowlibre.domain.port.in.interstitial;

import com.register.wowlibre.domain.dto.interstitial.*;

public interface InterstitialPort {
    InterstitialDto findAllActiveInterstitial(Long userId, String transactionId);

    void createInterstitial(String urlImg, String redirectUrl, String transactionId);

    void deleteInterstitial(Long interstitialId, String transactionId);
}
