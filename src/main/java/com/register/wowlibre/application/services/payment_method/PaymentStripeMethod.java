package com.register.wowlibre.application.services.payment_method;

import com.register.wowlibre.domain.dto.PaymentTransaction;
import com.register.wowlibre.domain.enums.PaymentStatus;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.PaymentGatewayModel;
import com.register.wowlibre.domain.port.out.stripe_credentials.ObtainStripeCredentials;
import com.register.wowlibre.domain.port.out.stripe_credentials.SaveStripeCredentials;
import com.register.wowlibre.infrastructure.entities.transactions.PaymentGatewaysEntity;
import com.register.wowlibre.infrastructure.entities.transactions.StripeCredentialsEntity;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Component
public class PaymentStripeMethod extends PaymentMethod {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStripeMethod.class);

    private final ObtainStripeCredentials obtainStripeCredentials;
    private final SaveStripeCredentials saveStripeCredentials;

    public PaymentStripeMethod(ObtainStripeCredentials obtainStripeCredentials,
                               SaveStripeCredentials saveStripeCredentials) {
        this.obtainStripeCredentials = obtainStripeCredentials;
        this.saveStripeCredentials = saveStripeCredentials;
    }

    @Override
    public PaymentGatewayModel payment(Long idMethodGateway, String currency, BigDecimal amount, Integer quantity,
                                       String productName, String referenceCode, String transactionId) {

        Optional<StripeCredentialsEntity> stripeCredential = obtainStripeCredentials.findByPayUCredentials(
                idMethodGateway,
                transactionId);

        if (stripeCredential.isEmpty()) {
            LOGGER.error("Stripe Credentials Invalid for gateway id: {}", idMethodGateway);
            throw new InternalException("PayUGateway Credentials Invalid", transactionId);
        }

        try {
            StripeCredentialsEntity stripe = stripeCredential.get();
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(stripe.getSuccessUrl())
                    .setCancelUrl(stripe.getCancelUrl())
                    .setClientReferenceId(referenceCode)
                    .setPaymentIntentData(
                            SessionCreateParams.PaymentIntentData.builder()
                                    .putMetadata("referenceCode", referenceCode)
                                    .build())
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(quantity.longValue())
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(currency)
                                    .setUnitAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(productName)
                                            .build())
                                    .build())
                            .build())
                    .build();
            Session session = Session.create(params);
            return PaymentGatewayModel.builder()
                    .id(session.getId())
                    .redirect(session.getUrl())
                    .build();
        } catch (StripeException e) {
            LOGGER.error("[PaymentStripeMethod] [payment] Error creating Stripe session: {}", e.getMessage());
            throw new InternalException(e.getMessage(), transactionId);
        }
    }

    @Override
    public void vinculate(PaymentGatewaysEntity paymentMethod,
                          Map<String, String> credentials, String transactionId) {
        StripeCredentialsEntity stripeCredentialsEntity = new StripeCredentialsEntity();
        stripeCredentialsEntity.setGateway(paymentMethod);
        stripeCredentialsEntity.setApiSecret(credentials.get("apiSecret"));
        stripeCredentialsEntity.setApiPublic(credentials.get("apiPublic"));
        stripeCredentialsEntity.setSuccessUrl(credentials.get("successUrl"));
        stripeCredentialsEntity.setCancelUrl(credentials.get("cancelUrl"));
        stripeCredentialsEntity.setWebhookUrl(credentials.get("webhookUrl"));
        stripeCredentialsEntity.setWebhookUrl(credentials.get("webhookSecret"));
        saveStripeCredentials.save(stripeCredentialsEntity, transactionId);
    }

    @Override
    public void delete(PaymentGatewaysEntity paymentMethod, String transactionId) {

        Optional<StripeCredentialsEntity> stripeCredentials = obtainStripeCredentials
                .findByPayUCredentials(paymentMethod.getId(), transactionId);

        if (stripeCredentials.isEmpty()) {
            throw new InternalException("Stripe Credentials Not Found", transactionId);
        }

        saveStripeCredentials.delete(stripeCredentials.get(), transactionId);
    }

    @Override
    public boolean validateCredentials(PaymentGatewaysEntity paymentGateway,
                                       PaymentTransaction paymentTransaction,
                                       String transactionId) {

        StripeCredentialsEntity stripeCred = obtainStripeCredentials
                .findByPayUCredentials(paymentGateway.getId(), transactionId)
                .orElseThrow(() -> new InternalException("Stripe Credentials Not Found", transactionId));

        final String payload = paymentTransaction.getStripePayment().getPayloadStripe();
        final String sigHeader = paymentTransaction.getSign();
        final String secret = stripeCred.getWebHookSecret();
        try {
            Webhook.constructEvent(payload, sigHeader, secret);
            return true;
        } catch (SignatureVerificationException e) {
            LOGGER.error("[PaymentStripeMethod] [payment] Firma inválida del webhook: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public PaymentStatus paymentStatus(PaymentTransaction paymentTransaction, String transactionId) {

        String status = paymentTransaction.getStripePayment().getStatus();
        Boolean paid = paymentTransaction.getStripePayment().getPaid();
        Boolean captured = paymentTransaction.getStripePayment().getCaptured();

        boolean pagoExitoso = "succeeded".equalsIgnoreCase(status)
                && Boolean.TRUE.equals(paid)
                && Boolean.TRUE.equals(captured);

        if (!pagoExitoso) {
            LOGGER.warn("❌ Pago no exitoso. Status: {}, Paid: {}, Captured: {}", status, paid, captured);
            return PaymentStatus.REJECTED;
        }

        return PaymentStatus.APPROVED;
    }

    @Override
    public PaymentStatus findByStatus(PaymentGatewaysEntity paymentGateway, String referenceCode, String sessionId,
                                      String transactionId) {

        StripeCredentialsEntity stripeCred = obtainStripeCredentials
                .findByPayUCredentials(paymentGateway.getId(), transactionId)
                .orElseThrow(() -> new InternalException("Stripe Credentials Not Found", transactionId));
        Stripe.apiKey = stripeCred.getApiSecret();
        try {
            Session session = Session.retrieve(sessionId);
            String paymentIntentId = session.getPaymentIntent();

            // Obtener el PaymentIntent para verificar su status
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            String status = paymentIntent.getStatus();

            LOGGER.info("🔍 Status del PaymentIntent: {}", status);

            // Mapear el status de Stripe a tu enum PaymentStatus
            return switch (status) {
                case "succeeded" -> PaymentStatus.APPROVED;
                case "requires_payment_method", "requires_confirmation", "requires_action", "processing" ->
                        PaymentStatus.PENDING;
                case "canceled", "payment_failed" -> PaymentStatus.REJECTED;
                default -> {
                    LOGGER.warn("⚠️ Status desconocido del PaymentIntent: {}", status);
                    yield PaymentStatus.PENDING;
                }
            };

        } catch (StripeException e) {
            LOGGER.error("[PaymentStripeMethod] [payment] Error al obtener el status del pago: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
