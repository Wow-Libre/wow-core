package com.register.wowlibre.application.services.transaction;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.account_game.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.enums.Currency;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.account_validation.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.payment_gateway.*;
import com.register.wowlibre.domain.port.in.products.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.domain.port.out.plans.*;
import com.register.wowlibre.domain.port.out.transactions.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Service
public class TransactionService implements TransactionPort {
    private static final String PRODUCT_IMG_DEFAULT = "https://static.wixstatic" +
            ".com/media/5dd8a0_cbcd4683525e448c8502b031dfce2527~mv2.webp";
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
    private final IntegratorPort integratorPort;
    /**
     * ACCOUNT VALIDATION PORT
     **/
    private final AccountValidationPort accountValidationPort;
    private final ObtainTransaction obtainTransaction;
    private final SaveTransaction saveTransaction;
    private final ProductPort productPort;
    private final RandomString randomString;
    private final ObtainPlan obtainPlan;
    private final PaymentGatewayPort paymentGatewayPort;

    public TransactionService(IntegratorPort integratorPort, AccountValidationPort accountValidationPort,
                              ObtainTransaction obtainTransaction, SaveTransaction saveTransaction,
                              ProductPort productPort, @Qualifier("subscriptionReference") RandomString randomString
            , ObtainPlan obtainPlan,
                              PaymentGatewayPort paymentGatewayPort) {
        this.integratorPort = integratorPort;
        this.accountValidationPort = accountValidationPort;
        this.obtainTransaction = obtainTransaction;
        this.saveTransaction = saveTransaction;
        this.productPort = productPort;
        this.randomString = randomString;
        this.obtainPlan = obtainPlan;
        this.paymentGatewayPort = paymentGatewayPort;
    }

    @Override
    public TransactionsDto transactionsByUserId(Long userId, Integer page, Integer size, String transactionId) {
        TransactionsDto data = new TransactionsDto();
        List<Transaction> transactions = obtainTransaction.findByUserId(userId, page, size, transactionId)
                .stream()
                .map(transaction -> new Transaction(transaction.getId(), transaction.getPrice(),
                        transaction.getCurrency(), transaction.getStatus(),
                        TransactionStatus.getType(transaction.getStatus()).getStatus(),
                        transaction.getCreationDate(), transaction.getReferenceNumber(),
                        productName(transaction),
                        urlProduct(transaction)))
                .toList();
        data.setTransactions(transactions);
        data.setSize(obtainTransaction.findByUserId(userId, transactionId));

        return data;
    }

    private String productName(TransactionEntity transaction) {

        if (transaction.getProductId() != null) {
            return transaction.getProductId().getName();
        }

        if (transaction.getSubscriptionId() != null && transaction.isSubscription()) {
            return Optional.ofNullable(transaction.getSubscriptionId().getPlanId().getName()).orElse("Azeroth Pass");
        }

        return "Azeroth Pass";
    }

    private String urlProduct(TransactionEntity transaction) {
        if (transaction.getProductId() != null) {
            return transaction.getProductId().getImageUrl();
        }
        return PRODUCT_IMG_DEFAULT;
    }

    @Override
    public Optional<TransactionEntity> findByReferenceNumberAndUserId(String referenceNumber, Long userId,
                                                                      String transactionId) {
        Optional<TransactionEntity> transaction = obtainTransaction.findByReferenceNumberAndUserId(referenceNumber,
                userId, transactionId);

        if (transaction.isEmpty()) {
            return Optional.empty();
        }

        TransactionEntity foundTransaction = transaction.get();

        boolean statusPaid = foundTransaction.getStatus().equalsIgnoreCase(TransactionStatus.PAID.getType())
                || foundTransaction.getStatus().equalsIgnoreCase(TransactionStatus.DELIVERED.getType());

        if (statusPaid) {
            return Optional.of(foundTransaction);
        }

        PaymentType paymentMethodType = PaymentType.valueOf(foundTransaction.getPaymentMethod());

        PaymentStatus paymentStatus = paymentGatewayPort.findByStatus(paymentMethodType,
                foundTransaction.getReferenceNumber(), foundTransaction.getReferencePayment(),
                transactionId);

        // Mapear el PaymentStatus a TransactionStatus
        switch (paymentStatus) {
            case APPROVED:
                if (!foundTransaction.getStatus().equalsIgnoreCase(TransactionStatus.PAID.getType())) {
                    foundTransaction.setStatus(TransactionStatus.PAID.getType());
                    LOGGER.info("✅ Pago aprobado para transacción: {}", foundTransaction.getReferenceNumber());
                }
                break;
            case PENDING:
                foundTransaction.setStatus(TransactionStatus.PENDING.getType());
                LOGGER.info("⏳ Pago pendiente para transacción: {}", foundTransaction.getReferenceNumber());
                break;
            case REJECTED:
                foundTransaction.setStatus(TransactionStatus.REJECTED.getType());
                LOGGER.warn("❌ Pago rechazado para transacción: {}", foundTransaction.getReferenceNumber());
                break;
            default:
                foundTransaction.setStatus(TransactionStatus.REJECTED.getType());
                LOGGER.warn("⚠️ Status de pago desconocido: {} para transacción: {}",
                        paymentStatus, foundTransaction.getReferenceNumber());
                break;
        }

        saveTransaction.save(foundTransaction);
        return Optional.of(foundTransaction);
    }

    @Override
    public void purchase(Long serverId, Long userId, Long accountId, String reference, List<ItemQuantityModel> items,
                         Double amount, String transactionId) {

        AccountVerificationDto accountVerificationDto = accountValidationPort.verifyAccount(userId, accountId, serverId,
                transactionId);

        RealmEntity realm = accountVerificationDto.realm();

        if (realm == null) {
            LOGGER.error("[TransactionService] [purchase] A realm has not been configured or the realm is disabled. " +
                    "transactionId: {}", transactionId);
            throw new InternalException("Realm is not available", transactionId);
        }

        integratorPort.purchase(realm.getHost(), realm.getJwt(), userId, accountId, reference, items, amount,
                transactionId);
    }

    @Override
    public void sendSubscriptionBenefits(Long serverId, Long userId, Long accountId, Long characterId,
                                         List<ItemQuantityModel> items, String benefitType, Double amount,
                                         String transactionId) {

        AccountVerificationDto accountVerificationDto = accountValidationPort.verifyAccount(userId, accountId, serverId,
                transactionId);

        RealmEntity realm = accountVerificationDto.realm();

        if (realm == null) {
            LOGGER.error("[TransactionService] [sendSubscriptionBenefits] A realm has not been configured or the " +
                    "realm is disabled. transactionId: {}", transactionId);
            throw new InternalException("Server is not available", transactionId);
        }

        integratorPort.sendBenefitsPremium(realm.getHost(), realm.getJwt(), userId, accountId, characterId, items,
                benefitType, amount, transactionId);
    }


    @Override
    public void save(TransactionEntity transaction, String transactionId) {
        saveTransaction.save(transaction);
    }

    @Override
    public Optional<TransactionEntity> findByReferenceNumber(String referenceNumber, String transactionId) {
        return obtainTransaction.findByReferenceNumber(referenceNumber, transactionId);
    }


    @Override
    public PaymentApplicableModel isRealPaymentApplicable(TransactionModel transaction, String transactionId) {
        final String orderId = randomString.nextString();

        if (transaction.isSubscription()) {
            String productReference = transaction.getProductReference();

            Optional<PlansEntity> planDetailDto = obtainPlan.findById(Long.valueOf(productReference), transactionId);

            if (planDetailDto.isEmpty()) {
                LOGGER.error("[TransactionService] [isRealPaymentApplicable] There is no active plan.  transactionId:" +
                        " {}", transaction);
                throw new InternalException("There is no active plan.", transactionId);
            }

            PlansEntity plan = planDetailDto.get();
            double discountPercentage = plan.getDiscount() / 100.0;
            double discountedPrice = plan.getPrice() * (1 - discountPercentage);
            final String currency = plan.getCurrency();
            final String description = String.format("Subscription %s", plan.getName());

            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setUserId(transaction.getUserId());
            transactionEntity.setAccountId(transaction.getAccountId());
            transactionEntity.setRealmId(transaction.getRealmId());
            transactionEntity.setPrice(discountedPrice);
            transactionEntity.setStatus(TransactionStatus.CREATED.getType());
            transactionEntity.setProductId(null);
            transactionEntity.setSubscriptionId(null);
            transactionEntity.setReferenceNumber(orderId);
            transactionEntity.setCreditPoints(false);
            transactionEntity.setSend(false);
            transactionEntity.setCreationDate(LocalDateTime.now());
            transactionEntity.setCurrency(currency);
            transactionEntity.setPlanId(plan.getId());
            transactionEntity.setSubscription(true);
            transactionEntity.setPaymentMethod(transaction.getPaymentType().getType());
            saveTransaction.save(transactionEntity);

            return new PaymentApplicableModel(discountedPrice > 0, discountedPrice, currency, orderId, description,
                    plan.getTax(),
                    plan.getReturnTax(), plan.getName(), transactionEntity);
        }

        String productReference = transaction.getProductReference();

        ProductEntity productDto = productPort.getProduct(productReference, transactionId);

        if (productDto == null) {
            LOGGER.error("[TransactionService] [isRealPaymentApplicable] Product not found productReferenceNumber: {}"
                    + " transactionId: {}", productReference, transaction);
            throw new InternalException("The product is not available for donation", transactionId);
        }

        final String description = productDto.getName();
        final boolean hasCreditPoints = productDto.isUseCreditPoints();
        Double price = productDto.getPrice();
        Integer discountPercentage = productDto.getDiscount();
        double discountAmount = ((double) discountPercentage / 100) * price;
        final Double finalPrice = price - discountAmount;
        final String currency = hasCreditPoints ? Currency.POINTS.name() : Currency.USD.name();
        final boolean isFree = finalPrice <= 0;

        final boolean isPaymentRedirectToCheckout = !isFree && !hasCreditPoints;

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setAccountId(transaction.getAccountId());
        transactionEntity.setRealmId(productDto.getRealmId());
        transactionEntity.setAccountId(transaction.getAccountId());
        transactionEntity.setStatus(TransactionStatus.CREATED.getType());
        transactionEntity.setProductId(productDto);
        transactionEntity.setCreationDate(LocalDateTime.now());
        transactionEntity.setReferenceNumber(orderId);
        transactionEntity.setPrice(finalPrice);
        transactionEntity.setSend(false);
        transactionEntity.setCurrency(currency);
        transactionEntity.setUserId(transaction.getUserId());
        transactionEntity.setPaymentMethod(transaction.getPaymentType().getType());
        saveTransaction.save(transactionEntity);

        return new PaymentApplicableModel(isPaymentRedirectToCheckout, finalPrice, currency, orderId, description,
                productDto.getTax(), productDto.getReturnTax(), productDto.getName(), transactionEntity);
    }
}
