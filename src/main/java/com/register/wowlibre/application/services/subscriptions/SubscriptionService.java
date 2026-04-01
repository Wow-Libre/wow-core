package com.register.wowlibre.application.services.subscriptions;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.benefit_premium.*;
import com.register.wowlibre.domain.port.in.subscriptions.*;
import com.register.wowlibre.domain.port.in.wowlibre.*;
import com.register.wowlibre.domain.port.out.*;
import com.register.wowlibre.domain.port.out.plans.*;
import com.register.wowlibre.domain.port.out.subscription_benefits.*;
import com.register.wowlibre.domain.port.out.subscriptions.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import com.register.wowlibre.infrastructure.util.*;
import jakarta.transaction.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Service
public class SubscriptionService implements SubscriptionPort {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SubscriptionService.class);

    private final SaveSubscription saveSubscription;
    private final ObtainSubscription obtainSubscription;

    private final JsonLoaderPort obtainJsonLoader;
    private final ObtainSubscriptionBenefits obtainSubscriptionBenefit;
    private final SaveSubscriptionBenefits saveSubscriptionBenefit;


    private final ObtainPlan obtainPlan;
    private final WowLibrePort wowLibrePort;

    private final RandomString randomString;
    private final BenefitPremiumPort benefitPremiumPort;

    public SubscriptionService(ObtainSubscription obtainSubscription, JsonLoaderPort obtainJsonLoader,
                               ObtainSubscriptionBenefits obtainSubscriptionBenefit,
                               SaveSubscriptionBenefits saveSubscriptionBenefit, WowLibrePort wowLibrePort,
                               SaveSubscription saveSubscription, ObtainPlan obtainPlan,
                               @Qualifier("productReference") RandomString randomString,
                               BenefitPremiumPort benefitPremiumPort) {
        this.obtainSubscription = obtainSubscription;
        this.obtainJsonLoader = obtainJsonLoader;
        this.obtainSubscriptionBenefit = obtainSubscriptionBenefit;
        this.saveSubscriptionBenefit = saveSubscriptionBenefit;
        this.wowLibrePort = wowLibrePort;
        this.saveSubscription = saveSubscription;
        this.obtainPlan = obtainPlan;
        this.randomString = randomString;
        this.benefitPremiumPort = benefitPremiumPort;
    }

    @Override
    public PillWidgetHomeDto getPillHome(Long userId, String language, String transactionId) {
        PillHomeModel pillHomeModel = obtainJsonLoader.getResourcePillHome(language, transactionId);

        if (userId != null) {

            Optional<SubscriptionEntity> subscriptionEntity = obtainSubscription.findByUserIdAndStatus(userId,
                    SubscriptionStatus.ACTIVE.getType());

            if (subscriptionEntity.isPresent()) {
                return new PillWidgetHomeDto(pillHomeModel.getActive(), "/accounts");
            }

        }
        return new PillWidgetHomeDto(pillHomeModel.getInactive(), "/subscriptions");
    }

    @Override
    public SubscriptionEntity createSubscription(Long userId, Long planId, String transactionId) {

        Optional<SubscriptionEntity> subscriptionEntity = obtainSubscription.findByUserIdAndStatus(userId,
                SubscriptionStatus.ACTIVE.getType());

        if (subscriptionEntity.isPresent()) {
            throw new InternalException("You already have an active subscription", transactionId);
        }

        Optional<PlansEntity> plan = obtainPlan.findById(planId, transactionId);

        if (plan.isEmpty()) {
            LOGGER.error("Plan not found: planId {} UserId {} TransactionId {}", planId, userId, transactionId);
            throw new InternalException("Plan not found", transactionId);
        }

        PlansEntity planEntity = plan.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDate nextInvoiceDate;
        if (planEntity.getFrequencyType().equalsIgnoreCase("YEARLY")) {
            nextInvoiceDate = now.plusYears(planEntity.getFrequencyValue()).toLocalDate();
        } else {
            nextInvoiceDate = now.plusMonths(planEntity.getFrequencyValue()).toLocalDate();
        }

        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setUserId(userId);
        subscription.setPlanId(planEntity);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setNextInvoiceDate(nextInvoiceDate);
        subscription.setReferenceNumber(randomString.nextString());
        subscription.setStatus(SubscriptionStatus.ACTIVE.getType());
        return saveSubscription.save(subscription, transactionId);
    }

    @Override
    public boolean isActiveSubscription(Long userId, String transactionId) {
        Optional<SubscriptionEntity> subscriptionEntity = obtainSubscription.findByUserIdAndStatus(userId,
                SubscriptionStatus.ACTIVE.getType());

        return subscriptionEntity.isPresent();
    }

    @Override
    public Optional<SubscriptionEntity> findActiveSubscription(Long userId, String transactionId) {
        return obtainSubscription.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE.getType());
    }

    @Override
    public SubscriptionBenefitsDto benefits(Long userId, Long realmId, String language, String transactionId) {

        if (!isActiveSubscription(userId, transactionId)) {
            return new SubscriptionBenefitsDto(new ArrayList<>(), 0);
        }

        List<BenefitsPremiumDto> benefits =
                benefitPremiumPort.findByLanguageAndRealmId(language, realmId, transactionId).stream()
                        .filter(benefit -> realmId.equals(benefit.realmId) && (benefit.reactivable
                                || obtainSubscriptionBenefit.findByUserIdAndBenefitIdAndServerId(userId,
                                benefit.id, realmId).isEmpty())).toList();

        return new SubscriptionBenefitsDto(benefits, benefits.size());
    }

    @Transactional
    @Override
    public void claimBenefits(Long realmId, Long userId, Long accountId, Long characterId, String language,
                              Long benefitId, String transactionId) {

        if (!isActiveSubscription(userId, transactionId)) {
            throw new InternalException("Subscription Inactive", transactionId);
        }

        Optional<BenefitsPremiumDto> benefits =
                benefitPremiumPort.findByLanguageAndRealmId(language, realmId, transactionId).stream()
                        .filter(benefit -> Objects.equals(benefit.id, benefitId)
                                && benefit.realmId.equals(realmId)).findAny();

        if (benefits.isEmpty()) {
            throw new InternalException("Benefits not available", transactionId);
        }

        BenefitsPremiumDto benefitModel = benefits.get();

        if (!benefitModel.reactivable) {
            if (obtainSubscriptionBenefit.findByUserIdAndBenefitIdAndServerId(userId, benefitModel.id, realmId).isPresent()) {
                throw new InternalException("benefit has been claimed", transactionId);
            }
            SubscriptionBenefitEntity subscriptionBenefit = new SubscriptionBenefitEntity();
            subscriptionBenefit.setBenefitId(benefitModel.id);
            subscriptionBenefit.setCreatedAt(LocalDate.now());
            subscriptionBenefit.setUserId(userId);
            subscriptionBenefit.setRealmId(benefitModel.realmId);
            saveSubscriptionBenefit.save(subscriptionBenefit);
        }

        List<ItemQuantityModel> items = new ArrayList<>();
        if (benefitModel.sendItem && benefitModel.items != null && !benefitModel.items.isEmpty()) {
            items = benefitModel.items.stream().map(benefit -> new ItemQuantityModel(benefit.getCode(),
                    benefit.getQuantity())).toList();
        }

        wowLibrePort.sendBenefitsPremium(realmId, userId, accountId, characterId, items, benefitModel.type.getType(),
                benefitModel.amount, transactionId);
    }

    @Override
    public List<SubscriptionEntity> findByExpirateSubscription() {
        return obtainSubscription.findByExpirateSubscription();
    }

    @Override
    public List<SubscriptionEntity> findByActiveSubscription() {
        return obtainSubscription.findByActiveSubscription();
    }

    @Override
    public void save(SubscriptionEntity subscription) {
        saveSubscription.save(subscription, "");
    }

    @Override
    public SubscriptionEntity updateNextInvoice(Long userId, Long planId, String transactionId) {

        Optional<SubscriptionEntity> subscriptionEntity = obtainSubscription.findByUserIdAndStatus(userId,
                SubscriptionStatus.ACTIVE.getType());

        if (subscriptionEntity.isEmpty()) {
            LOGGER.error("[SubscriptionService] [updateNextInvoice] No active subscription found: UserId {} " +
                    "TransactionId {}", userId, transactionId);
            throw new InternalException("The customer does not have an active subscription and cannot be assigned a "
                    + "higher nextInvoice", transactionId);
        }

        SubscriptionEntity subscription = subscriptionEntity.get();
        final Optional<PlansEntity> plan = obtainPlan.findById(planId, transactionId);

        if (plan.isEmpty()) {
            LOGGER.error("[SubscriptionService] [updateNextInvoice] Plan not found: planId {} UserId {} " +
                    "TransactionId {}", planId, userId, transactionId);
            throw new InternalException("The plan to link the subscription could not be found.", transactionId);
        }

        PlansEntity planEntity = plan.get();
        LocalDate nextInvoiceDate;
        if (planEntity.getFrequencyType().equalsIgnoreCase("YEARLY")) {
            nextInvoiceDate = subscription.getNextInvoiceDate().plusYears(planEntity.getFrequencyValue());
        } else {
            nextInvoiceDate = subscription.getNextInvoiceDate().plusMonths(planEntity.getFrequencyValue());
        }
        subscription.setNextInvoiceDate(nextInvoiceDate);
        saveSubscription.save(subscription, transactionId);
        return subscription;
    }

    @Override
    public SubscriptionAdminListDto getSubscriptionAdminList(String transactionId) {
        List<SubscriptionEntity> all = obtainSubscription.findAll();
        List<SubscriptionAdminDto> dtos = all.stream()
                .map(this::toAdminDto)
                .toList();
        return new SubscriptionAdminListDto(dtos.size(), dtos);
    }

    private SubscriptionAdminDto toAdminDto(SubscriptionEntity s) {
        var plan = s.getPlanId();
        return new SubscriptionAdminDto(
                s.getId(),
                s.getUserId(),
                s.getReferenceNumber(),
                s.getStatus(),
                plan != null ? plan.getName() : null,
                plan != null ? plan.getPrice() : null,
                plan != null ? plan.getCurrency() : null,
                plan != null ? plan.getFrequencyType() : null,
                plan != null ? plan.getFrequencyValue() : null,
                s.getCreatedAt(),
                s.getNextInvoiceDate()
        );
    }

}
