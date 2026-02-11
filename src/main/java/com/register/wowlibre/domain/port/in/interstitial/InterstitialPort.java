package com.register.wowlibre.domain.port.in.interstitial;

import com.register.wowlibre.domain.dto.interstitial.InterstitialAdminDto;
import com.register.wowlibre.domain.dto.interstitial.InterstitialDto;

import java.util.List;

public interface InterstitialPort {
    InterstitialDto findAllActiveInterstitial(Long userId, String transactionId);

    List<InterstitialAdminDto> findAllForAdmin(String transactionId);

    void createInterstitial(String urlImg, String redirectUrl, String transactionId);

    void deleteInterstitial(Long interstitialId, String transactionId);

    void updateInterstitial(Long interstitialId, String urlImg, String redirectUrl, String transactionId);
}
