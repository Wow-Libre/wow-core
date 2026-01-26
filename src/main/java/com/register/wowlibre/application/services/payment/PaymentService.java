package com.register.wowlibre.application.services.payment;


import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.payment.*;
import com.register.wowlibre.domain.port.in.payment_gateway.*;
import com.register.wowlibre.domain.port.in.subscriptions.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.domain.port.in.wallet.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.util.*;

import static com.register.wowlibre.domain.enums.PaymentType.*;


@Service
public class PaymentService implements PaymentPort {
    private final TransactionPort transactionPort;
    private final SubscriptionPort subscriptionPort;
    private final WalletPort walletPort;
    private final PaymentGatewayPort paymentGatewayPort;

    public PaymentService(TransactionPort transactionPort,
                          SubscriptionPort subscriptionPort, WalletPort walletPort,
                          PaymentGatewayPort paymentGatewayPort) {
        this.transactionPort = transactionPort;
        this.subscriptionPort = subscriptionPort;
        this.walletPort = walletPort;
        this.paymentGatewayPort = paymentGatewayPort;
    }

    @Override
    public void processPayment(PaymentTransaction paymentTransaction, PaymentType paymentType, String transactionId) {

        PaymentStatus paymentStatus = paymentGatewayPort.paymentStatus(paymentTransaction, paymentType, transactionId);

        Optional<TransactionEntity> transaction =
                transactionPort.findByReferenceNumber(paymentTransaction.getReferenceSale(), transactionId);

        if (transaction.isEmpty()) {
            throw new InternalException("transaction Not Found", transactionId);
        }

        TransactionEntity transactionUpdate = transaction.get();

        final boolean isSubscription = transactionUpdate.isSubscription();
        final Long userId = transactionUpdate.getUserId();

        switch (paymentStatus) {
            case APPROVED:
                transactionUpdate.setStatus(TransactionStatus.PAID.getType());

                if (isSubscription) {
                    SubscriptionEntity subscription =
                            subscriptionPort.createSubscription(transactionUpdate.getUserId(),
                                    Long.valueOf(transactionUpdate.getReferenceNumber()),
                                    transactionId);
                    transactionUpdate.setSubscriptionId(subscription);
                    transactionUpdate.setSend(true);
                }

                ProductEntity product = transactionUpdate.getProductId();

                if (product != null && product.getCreditPointsValue() != null) {
                    long pointsRecharge = product.getCreditPointsValue();
                    if (pointsRecharge > 0) {
                        Long currentPoints = walletPort.getPoints(userId, transactionId);
                        Long updatedPoints = (currentPoints != null ? currentPoints : 0) + pointsRecharge;
                        walletPort.addPoints(userId, updatedPoints, transactionId);
                        transactionUpdate.setSend(true);
                    }
                }

                transactionPort.save(transactionUpdate, transactionId);
                break;
            case REJECTED:
                transactionUpdate.setStatus(TransactionStatus.REJECTED.getType());
                transactionUpdate.setSend(true);
                transactionUpdate.setPaymentMethod(paymentTransaction.getPaymentMethodName());
                transactionPort.save(transactionUpdate, transactionId);
                break;
            default:
                transactionUpdate.setStatus(TransactionStatus.ERROR.getType());
                transactionUpdate.setSend(true);
                transactionUpdate.setPaymentMethod(paymentTransaction.getPaymentMethodName());
                transactionPort.save(transactionUpdate, transactionId);
                break;
        }
    }


    @Override
    public CreatePaymentRedirectDto createPayment(Long userId, String email, CreatePaymentDto createPaymentDto,
                                                  String transactionId) {
        PaymentType paymentType = PaymentType.getType(createPaymentDto.getPaymentType());

        if (paymentType.equals(NOT_MAPPED)) {
            throw new InternalException("Invalid payment type", transactionId);
        }

        PaymentApplicableModel paymentApplicableModel =
                transactionPort.isRealPaymentApplicable(TransactionModel.builder()
                        .isSubscription(createPaymentDto.getIsSubscription())
                        .accountId(createPaymentDto.getAccountId())
                        .paymentType(paymentType)
                        .realmId(createPaymentDto.getRealmId())
                        .productReference(createPaymentDto.getProductReference())
                        .userId(userId).build(), transactionId);

        if (paymentApplicableModel.isPayment()) {
            final String referenceCode = paymentApplicableModel.orderId();
            final String taxValue = paymentApplicableModel.tax();
            final Double amount = paymentApplicableModel.amount();
            BigDecimal amountBD = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
            final String currency = paymentApplicableModel.currency();
            final String returnTax = paymentApplicableModel.returnTax();

            PaymentGatewayModel paymentMethod = paymentGatewayPort.generateUrlPayment(paymentType, currency,
                    amountBD, 1, paymentApplicableModel.productName(), referenceCode,
                    transactionId);
            String accountId = paymentMethod.payu != null ? paymentMethod.payu.accountId() : null;
            String merchantId = paymentMethod.payu != null ? paymentMethod.payu.merchantId() : null;
            final String uuidReferencePayment = paymentMethod.id;

            TransactionEntity transactionEntity = paymentApplicableModel.transactionEntity();
            transactionEntity.setReferencePayment(uuidReferencePayment);
            transactionPort.save(transactionEntity, transactionId);

            return CreatePaymentRedirectDto.builder()
                    .redirect(paymentMethod.redirect)
                    .isPayment(true)
                    .confirmationUrl(paymentMethod.webhookUrl)
                    .responseUrl(paymentMethod.successUrl)
                    .buyerEmail(email)
                    .currency(currency)
                    .taxReturnBase(returnTax)
                    .tax(taxValue)
                    .amount(amountBD)
                    .referenceCode(referenceCode)
                    .description(paymentApplicableModel.description())
                    .payu(new PayUCredentialsModel(accountId, merchantId, paymentMethod.signature, "0"))
                    .build();

        }

        Optional<TransactionEntity> transaction =
                transactionPort.findByReferenceNumber(paymentApplicableModel.orderId(), transactionId);

        if (transaction.isEmpty() || transaction.get().isSubscription()) {
            throw new InternalException("Invalid Transaction", transactionId);
        }

        TransactionEntity transactionEntity = transaction.get();

        long points = walletPort.getPoints(userId, transactionId);

        if (transactionEntity.getPrice() > points) {
            transactionEntity.setStatus(TransactionStatus.INSUFFICIENT_MONEY.getType());
            transactionPort.save(transactionEntity, transactionId);
            throw new InternalException("SALDO INSUFICIENTE", transactionId);
        }

        Long pointsToUse = (long) (points - transactionEntity.getPrice());
        walletPort.addPoints(userId, pointsToUse, transactionId);
        transactionEntity.setStatus(TransactionStatus.PAID.getType());
        transactionPort.save(transactionEntity, transactionId);

        return CreatePaymentRedirectDto.builder().isPayment(false).redirect("/profile/purchases").build();
    }

}
