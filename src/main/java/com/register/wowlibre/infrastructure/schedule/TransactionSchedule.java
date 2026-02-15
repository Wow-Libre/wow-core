package com.register.wowlibre.infrastructure.schedule;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.machine.*;
import com.register.wowlibre.domain.port.in.packages.*;
import com.register.wowlibre.domain.port.in.subscriptions.*;
import com.register.wowlibre.domain.port.in.wallet.*;
import com.register.wowlibre.domain.port.in.wowlibre.*;
import com.register.wowlibre.domain.port.out.account_game.*;
import com.register.wowlibre.domain.port.out.transactions.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import jakarta.transaction.*;
import org.slf4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
public class TransactionSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionSchedule.class);

    private static final int MAX_ACCOUNTS_PER_USER = 500;

    private final ObtainTransaction obtainTransaction;
    private final SaveTransaction saveTransaction;
    private final WowLibrePort wowLibrePort;
    private final PackagesPort packagesPort;
    private final SubscriptionPort subscriptionPort;
    private final WalletPort walletPort;
    private final MachinePort machinePort;
    private final IntegratorPort integratorPort;
    private final ObtainAccountGamePort obtainAccountGamePort;

    public TransactionSchedule(ObtainTransaction obtainTransaction, SaveTransaction saveTransaction,
                               WowLibrePort wowLibrePort, PackagesPort packagesPort, SubscriptionPort subscriptionPort,
                               WalletPort walletPort, MachinePort machinePort, IntegratorPort integratorPort,
                               ObtainAccountGamePort obtainAccountGamePort) {
        this.obtainTransaction = obtainTransaction;
        this.saveTransaction = saveTransaction;
        this.wowLibrePort = wowLibrePort;
        this.packagesPort = packagesPort;
        this.subscriptionPort = subscriptionPort;
        this.walletPort = walletPort;
        this.machinePort = machinePort;
        this.integratorPort = integratorPort;
        this.obtainAccountGamePort = obtainAccountGamePort;
    }

    @Transactional
    @Scheduled(cron = "1/50 * * * * *")
    public void sendPurchases() {
        final String transactionId = UUID.randomUUID().toString();
        List<TransactionEntity> transactionEntities = obtainTransaction.findByStatusIsPaidAndSendIsFalse(transactionId);

        for (TransactionEntity transaction : transactionEntities) {
            try {

                if (transaction.isSubscription()) {

                    boolean activeSubscription = subscriptionPort.isActiveSubscription(transaction.getUserId(),
                            transactionId);

                    final SubscriptionEntity subscription;
                    if (activeSubscription) {
                        subscription = subscriptionPort.updateNextInvoice(transaction.getUserId(),
                                transaction.getPlanId(),
                                transactionId);
                    } else {
                        subscription = subscriptionPort.createSubscription(transaction.getUserId(),
                                transaction.getPlanId(), transactionId);
                    }
                    transaction.setSubscriptionId(subscription);
                    machinePort.addPointsSubscription(subscription.getUserId(), 100, transactionId);
                } else {
                    List<ItemQuantityModel> items = packagesPort.findByProductId(transaction.getProductId(),
                            transactionId);

                    double amount = transaction.isCreditPoints() ? transaction.getProductId().getCreditPointsValue()
                            : 0d;

                    ProductEntity product = transaction.getProductId();

                    if (product != null && product.getCreditPointsValue() != null) {
                        long pointsRecharge = product.getCreditPointsValue();
                        if (pointsRecharge > 0) {
                            Long currentPoints = walletPort.getPoints(transaction.getUserId(), transactionId);
                            Long updatedPoints = (currentPoints != null ? currentPoints : 0) + pointsRecharge;
                            walletPort.addPoints(transaction.getUserId(), updatedPoints, transactionId);
                        }
                    }

                    if (amount > 0 || !items.isEmpty()) {
                        wowLibrePort.sendPurchases(transaction.getRealmId(), transaction.getUserId(),
                                transaction.getAccountId(), amount, items, transaction.getReferenceNumber(),
                                transactionId);
                    }

                }
                transaction.setStatus(TransactionStatus.DELIVERED.getType());
                transaction.setSend(true);
                saveTransaction.save(transaction);
            } catch (Exception e) {
                LOGGER.error("Error Transaction Sends {}", e.getLocalizedMessage());
            }
        }

    }

    @Transactional
    @Scheduled(cron = "1/50 * * * * *")
    public void verifySubscriptions() {
        String transactionId = UUID.randomUUID().toString();

        List<SubscriptionEntity> subscriptions = subscriptionPort.findByExpirateSubscription();

        for (SubscriptionEntity subscription : subscriptions) {
            Long userId = subscription.getUserId();
            List<AccountGameEntity> accounts = obtainAccountGamePort.findByUserIdAndStatusIsTrue(
                    userId, 0, MAX_ACCOUNTS_PER_USER, transactionId);

            for (AccountGameEntity account : accounts) {
                try {
                    RealmEntity realm = account.getRealmId();
                    if (realm == null || realm.getHost() == null || realm.getJwt() == null) {
                        LOGGER.warn("[verifySubscriptions] userId={} accountId={} realm/host/jwt null, skip",
                                userId, account.getAccountId());
                        continue;
                    }
                    String host = realm.getHost();
                    String jwt = realm.getJwt();
                    Long accountId = account.getAccountId();

                    boolean isPremium = integratorPort.isPremiumRealm(host, jwt, accountId, transactionId);
                    if (isPremium) {
                        integratorPort.updatePremiumRealm(host, jwt, accountId, false, transactionId);
                        LOGGER.debug("[verifySubscriptions] createPremiumRealm userId={} accountId={} realm={}",
                                userId, accountId, realm.getName());
                    }
                } catch (Exception e) {
                    LOGGER.error("[verifySubscriptions] userId={} accountId={} error={}",
                            userId, account.getAccountId(), e.getMessage());
                }
            }
            subscription.setStatus(SubscriptionStatus.INACTIVE.getType());
            subscriptionPort.save(subscription);
        }


    }

    @Scheduled(cron = "0 */15 * * * *")
    public void realmVerifySubscription() {
        String transactionId = UUID.randomUUID().toString();
        List<SubscriptionEntity> subscriptions = subscriptionPort.findByActiveSubscription();

        for (SubscriptionEntity subscription : subscriptions) {
            Long userId = subscription.getUserId();
            List<AccountGameEntity> accounts = obtainAccountGamePort.findByUserIdAndStatusIsTrue(
                    userId, 0, MAX_ACCOUNTS_PER_USER, transactionId);

            for (AccountGameEntity account : accounts) {
                try {
                    RealmEntity realm = account.getRealmId();
                    if (realm == null || realm.getHost() == null || realm.getJwt() == null) {
                        LOGGER.warn("[realmVerifySubscription] userId={} accountId={} realm/host/jwt null, skip",
                                userId, account.getAccountId());
                        continue;
                    }
                    String host = realm.getHost();
                    String jwt = realm.getJwt();
                    Long accountId = account.getAccountId();

                    boolean isPremium = integratorPort.isPremiumRealm(host, jwt, accountId, transactionId);
                    if (!isPremium) {
                        integratorPort.updatePremiumRealm(host, jwt, accountId, true, transactionId);
                        LOGGER.debug("[realmVerifySubscription] createPremiumRealm userId={} accountId={} realm={}",
                                userId, accountId, realm.getName());
                    }
                } catch (Exception e) {
                    LOGGER.error("[realmVerifySubscription] userId={} accountId={} error={}",
                            userId, account.getAccountId(), e.getMessage());
                }
            }
        }
    }
}
