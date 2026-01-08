package com.register.wowlibre.application.services.interstitial;

import com.register.wowlibre.domain.dto.interstitial.InterstitialDto;
import com.register.wowlibre.domain.port.in.interstitial.InterstitialPort;
import com.register.wowlibre.domain.port.out.interstitial.ObtainInterstitial;
import com.register.wowlibre.domain.port.out.interstitial.SaveInterstitial;
import com.register.wowlibre.domain.port.out.interstitial_user.ObtainInterstitialUser;
import com.register.wowlibre.domain.port.out.interstitial_user.SaveInterstitialUser;
import com.register.wowlibre.infrastructure.entities.InterstitialEntity;
import com.register.wowlibre.infrastructure.entities.InterstitialUserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InterstitialService implements InterstitialPort {
    /**
     * Interstitial obtainer.
     */
    private final ObtainInterstitial obtainInterstitial;
    private final SaveInterstitial saveInterstitial;
    /**
     * Interstitial user saver.
     */
    private final SaveInterstitialUser saveInterstitialUser;
    private final ObtainInterstitialUser obtainInterstitialUser;

    public InterstitialService(ObtainInterstitial obtainInterstitial, SaveInterstitial saveInterstitial,
                               SaveInterstitialUser saveInterstitialUser,
                               ObtainInterstitialUser obtainInterstitialUser) {
        this.obtainInterstitial = obtainInterstitial;
        this.saveInterstitial = saveInterstitial;
        this.saveInterstitialUser = saveInterstitialUser;
        this.obtainInterstitialUser = obtainInterstitialUser;
    }

    @Override
    public InterstitialDto findAllActiveInterstitial(Long userId, String transactionId) {

        List<InterstitialEntity> activeInterstitials = obtainInterstitial.findAllActiveInterstitials(transactionId);

        List<InterstitialUserEntity> viewedByUser =
                obtainInterstitialUser.findByUserId(userId, transactionId);

        LocalDateTime now = LocalDateTime.now();

        for (InterstitialEntity interstitial : activeInterstitials) {

            Optional<InterstitialUserEntity> userView = viewedByUser.stream()
                    .filter(u -> u.getInterstitialId().getId().equals(interstitial.getId()))
                    .findFirst();

            if (userView.isEmpty()) {
                saveInterstitialUser.saveInterstitialUser(userId, interstitial, transactionId);
                return toDto(interstitial);
            }

            LocalDateTime lastViewed = userView.get().getViewedAt();
            if (lastViewed.plusHours(12).isBefore(now)) {
                saveInterstitialUser.saveInterstitialUser(userId, interstitial, transactionId);
                return toDto(interstitial);
            }
        }
        return null;
    }

    @Override
    public void createInterstitial(String urlImg, String redirectUrl, String transactionId) {
        saveInterstitial.saveInterstitial(urlImg, redirectUrl, transactionId);
    }

    @Override
    public void deleteInterstitial(Long interstitialId, String transactionId) {
        saveInterstitial.delete(interstitialId, transactionId);
    }

    @Override
    public void updateInterstitial(Long interstitialId, String urlImg, String redirectUrl, String transactionId) {
        saveInterstitial.updateInterstitial(interstitialId, urlImg, redirectUrl, transactionId);
    }

    private InterstitialDto toDto(InterstitialEntity entity) {
        return new InterstitialDto(
                entity.getId(),
                entity.getUrlImg(),
                entity.getRedirectUrl(),
                entity.isStatus()
        );
    }
}
