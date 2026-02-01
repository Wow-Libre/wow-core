package com.register.wowlibre.application.services.payment_method;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.client.payu.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.*;
import com.register.wowlibre.domain.port.out.payu_credentials.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.util.*;

@Component
public class PaymentPayUMethod extends PaymentMethod {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentPayUMethod.class);

    private final ObtainPayuCredentials payuCredentials;
    private final SavePayuCredentials savePayUCredentials;
    private final PayuPort payuPort;

    public PaymentPayUMethod(ObtainPayuCredentials payuCredentials, SavePayuCredentials savePayUCredentials,
                             PayuPort payuPort) {
        this.payuCredentials = payuCredentials;
        this.savePayUCredentials = savePayUCredentials;
        this.payuPort = payuPort;
    }


    @Override
    public PaymentGatewayModel payment(Long idMethodGateway, String currency, BigDecimal amount,
                                       Integer quantity, String productName,
                                       String referenceCode, String transactionId) {

        Optional<PayuCredentialsEntity> payUCredential = payuCredentials.findByPayUCredentials(idMethodGateway,
                transactionId);

        if (payUCredential.isEmpty()) {
            LOGGER.error("PayUGateway Credentials Not Found");
            throw new InternalException("PayUGateway Credentials Invalid", transactionId);
        }

        PayuCredentialsEntity payUCredentials = payUCredential.get();
        final String apiKey = payUCredentials.getApiKey();
        final String merchantId = payUCredentials.getMerchantId();
        final String accountId = payUCredentials.getAccountId();

        String formattedAmount = amount.toPlainString();

        final String concatenatedString =
                apiKey + "~" + merchantId + "~" + referenceCode + "~" + formattedAmount + "~" + currency;
        final String signature = PayUSignatureUtil.md5(concatenatedString);


        return PaymentGatewayModel.builder()
                .redirect(payUCredentials.getHost())
                .successUrl(payUCredentials.getSuccessUrl())
                .cancelUrl(payUCredentials.getCancelUrl())
                .webhookUrl(payUCredentials.getWebhookUrl())
                .signature(signature)
                .payu(PaymentGatewayModel.PayU.builder()
                        .accountId(accountId).merchantId(merchantId)
                        .build())
                .build();
    }

    @Override
    public void vinculate(PaymentGatewaysEntity paymentMethod,
                          Map<String, String> credentials, String transactionId) {

        PayuCredentialsEntity payuCredentialsEntity = new PayuCredentialsEntity();
        payuCredentialsEntity.setAccountId(credentials.get("accountId"));
        payuCredentialsEntity.setHost(credentials.get("host"));
        payuCredentialsEntity.setCancelUrl(credentials.get("cancelUrl"));
        payuCredentialsEntity.setGateway(paymentMethod);
        payuCredentialsEntity.setSuccessUrl(credentials.get("successUrl"));
        payuCredentialsEntity.setApiKey(credentials.get("apiKey"));
        payuCredentialsEntity.setApiLogin(credentials.get("apiLogin"));
        payuCredentialsEntity.setKeyPublic(credentials.get("keyPublic"));
        payuCredentialsEntity.setMerchantId(credentials.get("merchantId"));
        payuCredentialsEntity.setWebhookUrl(credentials.get("webhookUrl"));
        savePayUCredentials.save(payuCredentialsEntity, transactionId);
    }

    @Override
    public void delete(PaymentGatewaysEntity paymentMethod, String transactionId) {
        Optional<PayuCredentialsEntity> payuCredentialsEntity =
                payuCredentials.findByPayUCredentials(paymentMethod.getId(), transactionId);

        if (payuCredentialsEntity.isEmpty()) {
            LOGGER.error("PayU Credentials Not Found {} TransactionId {}", paymentMethod.getId(), transactionId);
            throw new InternalException("PayU Credentials Not Found", transactionId);
        }

        savePayUCredentials.delete(payuCredentialsEntity.get(), transactionId);
    }

    @Override
    public boolean validateCredentials(PaymentGatewaysEntity paymentGateway,
                                       PaymentTransaction paymentTransaction, String transactionId) {
        Optional<PayuCredentialsEntity> payuCredentialsEntity =
                payuCredentials.findByPayUCredentials(paymentGateway.getId(), transactionId);

        if (payuCredentialsEntity.isEmpty()) {
            LOGGER.error("[validateCredentials] PayU Credentials Not Found {} TransactionId {}",
                    paymentGateway.getId(), transactionId);
            throw new InternalException("PayU Credentials Not Found", transactionId);
        }

        PayuCredentialsEntity payUCredentials = payuCredentialsEntity.get();
        final String apiKey = payUCredentials.getApiKey();

        final String merchantId = paymentTransaction.getMerchantId();
        final String referenceSale = paymentTransaction.getReferenceSale();
        final String valueRaw = paymentTransaction.getValue();
        final String currency = paymentTransaction.getCurrency();
        final String statePol = paymentTransaction.getStatePol();
        final String signReceived = paymentTransaction.getSign();

        final String normalizedValue = PayUSignatureUtil.normalizeValue(valueRaw);

        final String signatureString = PayUSignatureUtil.buildSignatureString(
                apiKey, merchantId, referenceSale, normalizedValue, currency, statePol);

        final String expectedSign = PayUSignatureUtil.md5(signatureString);

        if (signReceived == null || !signReceived.equalsIgnoreCase(expectedSign)) {
            LOGGER.error("❌ Firma inválida. Notificación rechazada. " +
                            "expected=[{}], received=[{}], cadena=[{}]",
                    expectedSign, signReceived, signatureString);
            return false;
        }

        return true;
    }

    @Override
    public PaymentStatus paymentStatus(PaymentTransaction paymentTransaction, String transactionId) {
        return PaymentStatus.getType(paymentTransaction.getResponseMessagePol());
    }

    @Override
    public PaymentStatus findByStatus(PaymentGatewaysEntity paymentGateway, String referenceCode, String id,
                                      String transactionId) {
        PayuCredentialsEntity payUCredentials = payuCredentials
                .findByPayUCredentials(paymentGateway.getId(), transactionId)
                .orElseThrow(() -> new InternalException("Stripe Credentials Not Found", transactionId));

        PayUOrderDetailResponse response = payuPort.getOrderDetailByReference(referenceCode,
                payUCredentials.getApiLogin(),
                payUCredentials.getApiKey());

        // java
        String state = Optional.ofNullable(response)
                .map(PayUOrderDetailResponse::getResult)
                .map(PayUOrderDetailResponse.PayUResult::getPayload)
                .filter(payload -> !payload.isEmpty())
                .flatMap(payload -> payload.stream().findFirst())
                .flatMap(order -> Optional.ofNullable(order.getTransactions())
                        .flatMap(txList -> txList.stream().findFirst()))
                .map(tx -> Optional.ofNullable(tx.getTransactionResponse())
                        .map(PayUOrderDetailResponse.PayUTransactionResponse::getState)
                        .orElse(PaymentStatus.PENDING.getType()))
                .orElse(PaymentStatus.PENDING.getType());

        LOGGER.error("⚠️ Estado del PaymentIntent obtenido de PayU: {} ReferenceCode {} Response {}", state,
                referenceCode, response);

        return switch (state) {
            case "APPROVED" -> PaymentStatus.APPROVED;
            case "requires_payment_method", "requires_confirmation", "requires_action", "processing" ->
                    PaymentStatus.PENDING;
            case "canceled", "payment_failed" -> PaymentStatus.REJECTED;
            default -> {
                LOGGER.warn("⚠️ Status desconocido del PaymentIntent: {}", state);
                yield PaymentStatus.PENDING;
            }
        };
    }


}
