package com.register.wowlibre.infrastructure.repositories.promotion;

import com.register.wowlibre.domain.port.out.promotion.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaPromotionAdapter implements SavePromotion, ObtainPromotion {
    private final PromotionRepository promotionRepository;

    public JpaPromotionAdapter(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<PromotionEntity> findByPromotionRealmIdAndLanguage(Long realmId, String language,
            String transactionId) {
        return promotionRepository.findByLanguageAndRealmIdAndStatusIsTrue(language, realmId);
    }

    @Override
    public Optional<PromotionEntity> findById(Long id, String transactionId) {
        return promotionRepository.findById(id);
    }

    @Override
    public List<PromotionEntity> findByPromotionRealmId(Long realmId, String transactionId) {
        return promotionRepository.findByRealmIdAndStatusIsTrue(realmId);
    }

    @Override
    public List<PromotionEntity> findActiveByRealmId(Long realmId, String language, String transactionId) {
        if (language != null && !language.isEmpty()) {
            return promotionRepository.findByLanguageAndRealmIdAndStatusIsTrue(language, realmId);
        }
        return promotionRepository.findByRealmIdAndStatusIsTrue(realmId);
    }

    @Override
    public void save(PromotionEntity promotion) {
        promotionRepository.save(promotion);
    }
}
