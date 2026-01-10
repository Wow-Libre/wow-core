package com.register.wowlibre.infrastructure.repositories.interstitial;

import com.register.wowlibre.domain.port.out.interstitial.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaInterstitialAdapter implements SaveInterstitial, ObtainInterstitial {
    private final InterstitialRepository interstitialRepository;

    public JpaInterstitialAdapter(InterstitialRepository interstitialRepository) {
        this.interstitialRepository = interstitialRepository;
    }


    @Override
    public void saveInterstitial(String urlImg, String redirectUrl, String transactionId) {
        InterstitialEntity interstitialEntity = new InterstitialEntity();
        interstitialEntity.setStatus(true);
        interstitialEntity.setUrlImg(urlImg);
        interstitialEntity.setRedirectUrl(redirectUrl);
        interstitialRepository.save(interstitialEntity);
    }

    @Override
    public void updateInterstitial(Long id, String urlImg, String redirectUrl, String transactionId) {
        this.findById(id, transactionId).ifPresent(interstitialEntity -> {
            interstitialEntity.setUrlImg(urlImg);
            interstitialEntity.setRedirectUrl(redirectUrl);
            interstitialRepository.save(interstitialEntity);
        });
    }

    @Override
    public void delete(Long interstitialId, String transactionId) {
        this.findById(interstitialId, transactionId).ifPresent(interstitialEntity -> {
            interstitialEntity.setStatus(false);
            interstitialRepository.save(interstitialEntity);
        });
    }

    @Override
    public List<InterstitialEntity> findAllActiveInterstitials(String transactionId) {
        return interstitialRepository.findByStatusTrue();
    }

    @Override
    public Optional<InterstitialEntity> findById(Long id, String transactionId) {
        return interstitialRepository.findById(id);
    }
}
