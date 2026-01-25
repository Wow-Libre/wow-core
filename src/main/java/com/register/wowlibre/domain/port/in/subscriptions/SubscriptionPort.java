package com.register.wowlibre.domain.port.in.subscriptions;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.infrastructure.entities.transactions.SubscriptionEntity;

import java.util.*;

public interface SubscriptionPort {

    PillWidgetHomeDto getPillHome(Long userId, String language, String transactionId);

    SubscriptionEntity createSubscription(Long userId, Long planId, String transactionId);

    boolean isActiveSubscription(Long userId, String transactionId);

    SubscriptionBenefitsDto benefits(Long userId, Long serverId, String language, String transactionId);

    void claimBenefits(Long serverId, Long userId, Long accountId, Long characterId, String language, Long benefitId,
                       String transactionId);

    List<SubscriptionEntity> findByExpirateSubscription();

    void save(SubscriptionEntity subscription);
}
