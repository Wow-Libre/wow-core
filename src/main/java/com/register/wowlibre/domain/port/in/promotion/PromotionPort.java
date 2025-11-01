package com.register.wowlibre.domain.port.in.promotion;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface PromotionPort {
    List<PromotionModel> findByPromotionServerIdAndClassIdAndLanguage(Long realmId, Long classId, String language,
            String transactionId);

    PromotionModel findByPromotionServerIdAndLanguage(Long id, Long realmId, String language,
            String transactionId);

    List<PromotionModel> findByPromotionServerId(Long realmId, String transactionId);

    void create(CreatePromotionDto createPromotionDto, String transactionId);

    List<PromotionModel> findActiveByRealmId(Long realmId, String language, String transactionId);

    void deleteLogical(Long promotionId, String transactionId);
}
