package com.register.wowlibre.infrastructure.schedule;

import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.packages.*;
import com.register.wowlibre.domain.port.in.subscriptions.*;
import com.register.wowlibre.domain.port.in.wallet.*;
import com.register.wowlibre.domain.port.in.wowlibre.*;
import com.register.wowlibre.domain.port.out.transactions.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import jakarta.transaction.*;
import org.slf4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
public class TransactionSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionSchedule.class);

    private final ObtainTransaction obtainTransaction;
    private final SaveTransaction saveTransaction;
    private final WowLibrePort wowLibrePort;
    private final PackagesPort packagesPort;
    private final SubscriptionPort subscriptionPort;
    private final WalletPort walletPort;

    public TransactionSchedule(ObtainTransaction obtainTransaction, SaveTransaction saveTransaction,
                               WowLibrePort wowLibrePort, PackagesPort packagesPort, SubscriptionPort subscriptionPort,
                               WalletPort walletPort) {
        this.obtainTransaction = obtainTransaction;
        this.saveTransaction = saveTransaction;
        this.wowLibrePort = wowLibrePort;
        this.packagesPort = packagesPort;
        this.subscriptionPort = subscriptionPort;
        this.walletPort = walletPort;
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
        List<SubscriptionEntity> subscriptions = subscriptionPort.findByExpirateSubscription();

        subscriptions.forEach(subscription -> {
            subscription.setStatus(SubscriptionStatus.INACTIVE.getType());
            subscriptionPort.save(subscription);
        });

    }
}
