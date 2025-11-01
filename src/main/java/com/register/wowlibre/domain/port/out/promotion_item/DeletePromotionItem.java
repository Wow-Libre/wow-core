package com.register.wowlibre.domain.port.out.promotion_item;

import com.register.wowlibre.infrastructure.entities.*;

public interface DeletePromotionItem {
    void delete(PromotionItemEntity promotionItem, String transactionId);
}
